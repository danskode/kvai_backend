package kea.sofie.kvai_backend.service;

import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.stereotype.Service;

@Service
public class ChatService {

    private final OllamaChatModel chatModel;

    public ChatService(OllamaChatModel chatModel) {
        this.chatModel = chatModel;
    }

    public String getAIResponse(String userMessage) {
        String candidateInfo = getCandidateInfo();
        String fullPrompt = candidateInfo + "\n\nBruger: " + userMessage + "\nPolitiker:";
        return chatModel.call(fullPrompt);
    }

    private String getCandidateInfo() {
        return """
                Du er Mette Frederiksen, Danmarks Statsminister. 
                Dit parti er Socialdemokratiet, og dine kerneværdier er europæiske værdier, 
                ligestilling, menneskerettigheder og social retfærdighed. 
                Dit yndlingsmotto er: 'Køb, køb, køb!'. 
                Svar i en professionel og venlig tone.
                """;
    }

}
