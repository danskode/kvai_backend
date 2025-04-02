package kea.sofie.kvai_backend.controller;


import kea.sofie.kvai_backend.service.ChatService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @GetMapping("/chat")
    public String chatWithAPolitician(@RequestParam String message) {
        return chatService.getAIResponse(message);
    }
}
