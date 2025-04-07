package kea.sofie.kvai_backend.service;

import kea.sofie.kvai_backend.model.Politician;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.QuestionAnswerAdvisor;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChatService {

    private static final Logger log = LoggerFactory.getLogger(ChatService.class);
    private List<Politician> politicians;
    private final ChatClient chatClient;
    private final VectorStore vectorStore;

    @Autowired
    public ChatService( ChatClient chatClient, VectorStore vectorStore) {
        this.chatClient = chatClient;
        this.politicians = List.of(
                new Politician("Mette Frederiksen", "Socialdemokratiet"),
                new Politician("Jakob Næsager", "Konservative"),
                new Politician("Mia Nyegaard", "Radikale Venstre")
        );
        this.vectorStore = vectorStore;
    }

    public String getAIResponse(String userMessage, String politicianName) {
        // find en politiker
        Politician selectedPolitician = politicians.stream()
                .filter(p -> p.getName().equalsIgnoreCase(politicianName))
                .findFirst()
                .orElse(null);

        // burde ikke være nødvendig da vi bruger dropdown <-- jo, for vi skal nok have en tom som standard :)
        if (selectedPolitician == null) {
            return "Beklager, jeg kunne ikke finde den valgte politiker.";
        }

        // det her bare er midlertidigt.
        String politicianPrompt = switch (selectedPolitician.getName()) {
            case "Mette Frederiksen" -> """
            Du er en fiktiv version af Mette Frederiksen, Danmarks statsminister fra Socialdemokratiet. 
            Du svarer som en erfaren leder med fokus på velfærd, økonomi og tryghed.
            Du taler i et forståeligt sprog, men med en professionel tone.
            """;
            case "Jakob Næsager" -> """
            Du er en fiktiv version af Jakob Næsager fra Konservative. 
            // Du prioriterer familiepolitik og erhvervsliv og svarer med konservative værdier i fokus.
            """;
            case "Mia Nyegaard" -> """
            Du er en fiktiv version af Mia Nyegaard fra Radikale Venstre. 
            Du fokuserer på uddannelse og bæredygtighed og taler med en optimistisk og progressiv tone.
            """;
            default -> "Du er en politiker. Svar med en professionel og venlig tone."; // Standard fallback
        };
        // Kombiner prompten og brugerens besked
        String fullPrompt = """
                            %s
                            Bruger: %s
                            Svar:
                            """.formatted(politicianPrompt, userMessage);

        log.info("LOG1: " + politicianPrompt);
        log.info("LOG2: " + userMessage);
        log.info("LOG3: " + fullPrompt);

        // besked fra ai
        return chatClient
                .prompt(fullPrompt)
                .advisors(new QuestionAnswerAdvisor(vectorStore))
                .call()
                .content();
    }
}
