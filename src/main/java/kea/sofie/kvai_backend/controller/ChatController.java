package kea.sofie.kvai_backend.controller;

import kea.sofie.kvai_backend.model.ChatResponse;
import kea.sofie.kvai_backend.model.MessageRequest;
import kea.sofie.kvai_backend.service.ChatService;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.QuestionAnswerAdvisor;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin("*")
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

//    public ChatController(ChatService chatService, VectorStore vectorStore, OllamaChatModel ollamaChatModel) {
//        this.chatService = chatService;
//        this.vectorStore = vectorStore;
//        this.ollamaChatModel = ollamaChatModel;
//    }


    @PostMapping("/chat")
    public ResponseEntity<ChatResponse> chatWithCandidate(@RequestBody MessageRequest messageRequest) {
        String aiResponse = chatService.getAIResponse(messageRequest.getMessage(), messageRequest.getPolitician());
        return ResponseEntity.ok(new ChatResponse(aiResponse));
    }


    //Denne er nu nogenlunde klar ...
    @PostMapping("/ragtest")
    public String ragTest(@RequestBody String message) {
        return ChatClient.builder(ollamaChatModel)
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
                        SearchRequest.builder().similarityThreshold(0.8d).topK(6).build()))
                .user(message)
                .call()
                .content();
    }
}

