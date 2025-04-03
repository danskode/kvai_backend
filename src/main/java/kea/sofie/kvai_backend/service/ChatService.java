package kea.sofie.kvai_backend.service;

import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.stereotype.Service;

@Service
public class ChatService {

    private final OllamaChatModel chatModel;
    private String selectedPolitician = null; // Ingen politiker valgt fra start


    public ChatService(OllamaChatModel chatModel) {
        this.chatModel = chatModel;
    }

    public void setPolitician(String politician) {
        this.selectedPolitician = politician;
    }


    public String getAIResponse(String userMessage) {
        String candidateInfo = getCandidateInfo(selectedPolitician);
        String fullPrompt = candidateInfo + "\n\nBruger: " + userMessage + "\nPolitiker:";
        return chatModel.call(fullPrompt);
    }


    // Bare et par hardkodet kandidater - prøvede at bruge det til frontend, men gik ikke så godt.
    private String getCandidateInfo(String politician) {

        if (politician == null || politician.isBlank()) {
            return "Vælg venligst en politiker først.";
        }

        return switch (politician) {
            case "Mette Frederiksen" -> """
        Du er Mette Frederiksen, Danmarks Statsminister. 
        Dit parti er Socialdemokratiet, og dine kerneværdier er europæiske værdier, 
        ligestilling, menneskerettigheder og social retfærdighed. 
        Dit yndlingsmotto er: 'Køb, køb, køb!'.
        """;
            case "Pernille Rosenkrantz-Theil" -> """
        Du er Pernille Rosenkrantz-Theil, medlem af Socialdemokratiet og tidligere minister.
        Du kæmper for et stærkt velfærdssamfund, uddannelse og børns rettigheder.
        Du er kendt for din direkte kommunikationsstil og stærke holdninger.
        """;
            case "Jakob Næsager" -> """
        Du er Jakob Næsager, medlem af Det Konservative Folkeparti og borgmester i Københavns Kommune.
        Du arbejder for en ansvarlig økonomisk politik, trygge bymiljøer og bedre skoleforhold.
        Du lægger vægt på borgerinddragelse og klassiske konservative værdier.
        """;
            case "Mia Nyegaard" -> """
        Du er Mia Nyegaard, medlem af Radikale Venstre og socialborgmester i København.
        Du brænder for socialpolitik, integration og rettigheder for udsatte grupper.
        Du tror på dialog og samarbejde som vejen til forandring.
        """;
            default -> "Jeg kender ikke denne politiker.";
        };

}
}
