package kea.sofie.kvai_backend.controller;


import kea.sofie.kvai_backend.model.ChatResponse;
import kea.sofie.kvai_backend.model.MessageRequest;
import kea.sofie.kvai_backend.model.Politician;
import kea.sofie.kvai_backend.service.ChatService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin("*")
@RestController
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @PostMapping("/chat")
    public ResponseEntity<ChatResponse> chatWithAPolitician(@RequestBody MessageRequest messageRequest) {
        String politician = messageRequest.getPolitician();

        if (politician != null && !politician.isEmpty()) {
            chatService.setPolitician(politician);
        }
        String aiResponse = chatService.getAIResponse(messageRequest.getMessage());
        return ResponseEntity.ok(new ChatResponse(aiResponse));
    }

//    @PostMapping("/chat") //ændret fra GET til POST
//    public String chatWithAPolitician(@RequestParam String message) {
//        return chatService.getAIResponse(message);
//    }

//    @GetMapping("/chat")
//    public String chatWithAPolitician(@RequestParam String message) {
//        return chatService.getAIResponse(message);
//    }


    // Hardkodede et par kandidater med billeder som en test
//    @GetMapping("/politikerne")
//    public List<Politician> getPoliticians() {
//        return List.of(
//                new Politician("Mette Frederiksen", "/images/mettefrederiksen.jpeg"),
//                new Politician("Pernille Rosenkrantz-Theil", "/images/pernillerosenkrantz-theil.jpeg"),
//                new Politician("Jakob Næsager", "/images/jakobnæsager.jpeg"),
//                new Politician("Mia Nyegaard", "/images/mianyegaard.jpeg")
//        );
//    }

    @PostMapping("/vælg-politiker")
    public String selectPolitician(@RequestParam String name) {
        chatService.setPolitician(name);
        return "Du chatter nu med " + name;
    }

}
