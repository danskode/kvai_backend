package kea.sofie.kvai_backend.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import kea.sofie.kvai_backend.model.ChatResponse;
import kea.sofie.kvai_backend.model.MessageRequest;
import kea.sofie.kvai_backend.service.ChatService;

import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.InMemoryChatMemory;
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


    public ChatController(ChatService chatService, VectorStore vectorStore) {
        this.chatService = chatService;
        this.vectorStore = vectorStore;
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
}



