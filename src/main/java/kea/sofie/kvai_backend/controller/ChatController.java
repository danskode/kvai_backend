package kea.sofie.kvai_backend.controller;

import kea.sofie.kvai_backend.model.ChatResponse;
import kea.sofie.kvai_backend.model.MessageRequest;
import kea.sofie.kvai_backend.service.ChatService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin("*")
@RestController
@RequestMapping("/api")
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @PostMapping("/chat")
    public ResponseEntity<ChatResponse> chatWithCandidate(@RequestBody MessageRequest messageRequest) {
        String aiResponse = chatService.getAIResponse(messageRequest.getMessage(), messageRequest.getPolitician());
        return ResponseEntity.ok(new ChatResponse(aiResponse));
    }
}

