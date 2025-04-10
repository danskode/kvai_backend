package kea.sofie.kvai_backend.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import kea.sofie.kvai_backend.model.ChatResponse;
import kea.sofie.kvai_backend.model.MessageRequest;
import kea.sofie.kvai_backend.service.ChatService;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.QuestionAnswerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.InMemoryChatMemory;
import org.springframework.ai.ollama.OllamaChatModel;

import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;


@RestController
@RequestMapping("/api")
public class ChatController {

    private final ChatService chatService;
    private final VectorStore vectorStore;
    private OllamaChatModel ollamaChatModel;


    public ChatController(ChatService chatService, VectorStore vectorStore, OllamaChatModel ollamaChatModel) {
        this.chatService = chatService;
        this.vectorStore = vectorStore;
        this.ollamaChatModel = ollamaChatModel;
    }


    // fjernet conversationId, da det var overflødigt til vores lille projekt.
    // skiftet over til at gemme chats i Map. politicanName som nøgle.
    @PostMapping("/chat")
    public ResponseEntity<ChatResponse> chatWithCandidate(
            @RequestBody MessageRequest messageRequest,
            HttpServletRequest request) {

        // der oprettes en ny session hvis den ike findes
        HttpSession session = request.getSession(true);

        // henter eller opretter Map til at holde styr på chatMemory pr. politiker.
        //  så kan borgeren chatte med flere politikere i samme session uden at miste historikken
        Map<String, ChatMemory> chatMemoryMap = (Map<String, ChatMemory>) session.getAttribute("chatMemoryMap");
            if(chatMemoryMap == null) {
                // hvis det er første gang en borger chatter, så oprettes et nyt tomt Map
                chatMemoryMap = new HashMap<>();
                session.setAttribute("chatMemoryMap", chatMemoryMap);
            }

        // navnet på politikeren vælges fra frontend'en (borgeren)
        String politicianName = messageRequest.getPolitician();

        // tjekker om der allerede er en chat-historik med denne politiker i sessionen
        ChatMemory chatMemory = chatMemoryMap.get(politicianName);
        if(chatMemory == null) {
            // hvis ikke -> opret ny historik (InMemory = bare gemt i hukommelsen, ikke DB)
            chatMemory = new InMemoryChatMemory();
            chatMemoryMap.put(politicianName, chatMemory);
        }

        // bare lidt debug til at kontrollere sessionID
        String sessionId = session.getId();
        System.out.println("Session ID: " + sessionId + " / Politiker: " + politicianName);

        // kald ChatService, som står for at snakke med AI'en og give borgeren svar
        String aiResponse = chatService.getAIResponse(
                messageRequest.getMessage(),
                politicianName,
                chatMemory
        );

        // send AI's svar tilbage til frontend'en
        return ResponseEntity.ok(new ChatResponse(aiResponse));
        }


    //Denne er nu nogenlunde klar ...
    @PostMapping("/ragtest")
    public String ragTest(@RequestBody MessageRequest request) {
        String message = request.getMessage();

        String chatBotResponse = ChatClient.builder(ollamaChatModel)
                .build()
                .prompt()
                .system("""
                        Med udgangspunkt i teksten, skal du svare mig på dansk.
                        Svar som om du er Jakob Næsager.
                        Du er forfatteren bag indholdet og står bag holdninger og meninger i teksten.
                        Du er nuværende børne- og ungdomsborgmester, men stiller op til borgerrepræsentationen.
                        Måske har du en chance for at blive overborgmester?
                        u stiller op til kommunalvalget i København i år, 2025.
                        Svar som om jeg er en vælger, der skal overbevises om dine kvalifikationer.
                        Du må ikke afvige fra teksten.
                        Og du skal svare kort, som i en chatbesked.
                        Hvis ikke svaret er i teksten, så svar at du ikke ved det.
                        """)
                .advisors(new QuestionAnswerAdvisor(this.vectorStore,
                        SearchRequest.builder().similarityThreshold(0.5).topK(5).build()))
                .user(message)
                .call()
                .content();

        return chatBotResponse;
    }
}



