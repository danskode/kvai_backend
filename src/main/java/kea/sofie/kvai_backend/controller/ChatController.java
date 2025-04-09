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
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.ollama.OllamaChatModel;

import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;


@RestController
@RequestMapping("/api")
public class ChatController {

    private final ChatService chatService;
    private final VectorStore vectorStore;
    private final ChatModel chatModel;
    private OllamaChatModel ollamaChatModel;


    public ChatController(ChatService chatService, VectorStore vectorStore, OllamaChatModel ollamaChatModel, ChatModel chatModel) {
        this.chatService = chatService;
        this.vectorStore = vectorStore;
        this.ollamaChatModel = ollamaChatModel;
        this.chatModel = chatModel;
    }


    @PostMapping("/chat")
    public ResponseEntity<ChatResponse> chatWithCandidate(
            @RequestBody MessageRequest messageRequest,
            HttpServletRequest request) {
        HttpSession session = request.getSession(false); // der oprettes ikke en ny session hvis den ikke findes
        if (session == null) {
            //hvis session ikke findes -> opret en ny
            session = request.getSession(true);
        }


        ChatMemory chatMemory = (ChatMemory) session.getAttribute("chatMemory");
        // hvis der ikke findes en chat i denne session, så opretter den en ny og gemmer i sessionen.
        if (chatMemory == null) {
            chatMemory = new InMemoryChatMemory();
            session.setAttribute("chatMemory", chatMemory);
        }

        String conversationId = (String) session.getAttribute("conversationId");

        String sessionId = session.getId();

        System.out.println("SessionId: " + sessionId);

        if (conversationId == null) {
            conversationId = UUID.randomUUID().toString();
            session.setAttribute("conversationId", conversationId);
            System.out.println("Nyt conversationId genereret: " + conversationId);
        } else {
            System.out.println("Bruger et eksisterende conversationId: " + conversationId);
        }

        String aiResponse = chatService.getAIResponse(
                messageRequest.getMessage(),
                messageRequest.getPolitician(),
                conversationId,
                chatMemory
        );

        System.out.println("SESSION ID: " + sessionId + " Conversation ID: " + conversationId);

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



