package kea.sofie.kvai_backend.controller;

import kea.sofie.kvai_backend.model.Politician;
import kea.sofie.kvai_backend.service.ChatService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class PoliticianController {


    private final ChatService chatService;

    public PoliticianController(ChatService chatService) {
        this.chatService = chatService;
    }


    @GetMapping("/politikere")
    public List<Politician> getAllPoliticians() {
        return chatService.getPoliticians();
    }




}
