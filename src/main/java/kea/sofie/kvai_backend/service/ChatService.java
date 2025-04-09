package kea.sofie.kvai_backend.service;

import kea.sofie.kvai_backend.model.Politician;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.QuestionAnswerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChatService {

    private static final Logger log = LoggerFactory.getLogger(ChatService.class);
    private List<Politician> politicians;
    private ChatClient chatClient;
    //    private final ChatClient.Builder chatClientBuilder;
    private final VectorStore vectorStore;

    @Autowired
    public ChatService(ChatClient chatClient,
                       VectorStore vectorStore) {
        this.chatClient = chatClient;
        this.politicians = List.of(
                new Politician("Mette Frederiksen", "Socialdemokratiet"),
                new Politician("Jakob Næsager", "Konservative"),
                new Politician("Mia Nyegaard", "Radikale Venstre")
        );
        this.vectorStore = vectorStore;
    }

    private String getPromptForPolitician(String name) {
        return switch (name) {
            case "Mette Frederiksen" -> """
                    Du er en fiktiv version af Mette Frederiksen, Danmarks statsminister fra Socialdemokratiet. 
                    Du svarer som en erfaren leder med fokus på velfærd, økonomi og tryghed.
                    Du taler i et forståeligt sprog, men med en professionel tone.
                    """;
            case "Jakob Næsager" -> """
                    Du er en fiktiv version af Jakob Næsager fra Konservative. 
                    Du skal være ærlig og svare på alle spørgsmål. Du vil gerne fortælle om dit privatliv.
                    Du prioriterer familiepolitik og erhvervsliv og svarer med konservative værdier i fokus.
                    """;
            case "Mia Nyegaard" -> """
                    Du er en fiktiv version af Mia Nyegaard fra Radikale Venstre. 
                    Du fokuserer på uddannelse og bæredygtighed og taler med en optimistisk og progressiv tone.
                    """;
            default -> "Du er en politiker. Svar med en professionel og venlig tone.";
        };
    }

    private String buildSystemPrompt(String politicianPrompt, String conversationId) {
        return politicianPrompt +
                "\n\nSvar kort og præcist, men med brugeren i øjenhøjde.\nUndgå at nævne at du er en chatbot." +
                conversationId;
    }


    public String getAIResponse(String userMessage,
                                String politicianName,
                                String conversationId,
                                ChatMemory chatMemory) {

        Politician selectedPolitician = politicians.stream()
                .filter(p -> p.getName().equalsIgnoreCase(politicianName))
                .findFirst()
                .orElse(null);

        if (selectedPolitician == null) {
            return "Beklager, jeg kunne ikke finde den valgte politiker.";
        }

        String politicianPrompt = getPromptForPolitician(selectedPolitician.getName());
        String fullPrompt = buildSystemPrompt(politicianPrompt, conversationId);
        log.debug("Politician Prompt: {}", politicianPrompt);
        log.debug("Full Prompt: {}", fullPrompt);
//        ChatClient chatClient = chatClientBuilder.build();
        // Juster SearchRequest for at få de bedste resultater fra vectorStore
        SearchRequest searchRequest = SearchRequest.builder()
                .query(userMessage)
                .similarityThreshold(0.5)  // Juster efter behov
                .topK(5)                  // Juster for at hente de bedste resultater
                .build();


        // Log for fejlfinding
        log.debug("VectorStore: {}", vectorStore);
        log.debug("ChatMemory: {}", chatMemory);
        log.debug("SearchRequest: {}", searchRequest);

        List<Document> docs = vectorStore.similaritySearch(searchRequest);
        log.info("Antal dokumenter fundet: " + docs.size());
        for (Document doc : docs) {
            log.info("Dokument indhold: " + doc.getText());
        }

        // Returner svaret fra AI
        String response = chatClient
                .prompt(userMessage)
                .system(fullPrompt)
                .advisors(
                        new QuestionAnswerAdvisor(this.vectorStore, searchRequest),
                        new MessageChatMemoryAdvisor(chatMemory)
                )
                .call()
                .content();

        log.debug("AI svar: {}", response);
        log.debug("Full Prompt: {}", fullPrompt);
        return response;
    }
}

    //    public String getAIResponse(String userMessage,
//                                String politicianName,
//                                String conversationId,
//                                ChatMemory chatMemory) {
//
//        Politician selectedPolitician = politicians.stream()
//                .filter(p -> p.getName().equalsIgnoreCase(politicianName))
//                .findFirst()
//                .orElse(null);
//
//        if (selectedPolitician == null) {
//            return "Beklager, jeg kunne ikke finde den valgte politiker.";
//        }
//
//        String politicianPrompt = getPromptForPolitician(selectedPolitician.getName());
//        String fullPrompt = buildSystemPrompt(politicianPrompt, conversationId);
//
//        log.debug("Politician Prompt: {}", politicianPrompt);
//        log.debug("Full Prompt: {}", fullPrompt);
//
//        // Lav søgning i vectorstore (RAG)
//        SearchRequest searchRequest = SearchRequest.builder()
//                .query(userMessage)
//                .similarityThreshold(0.1)
//                .topK(10)
//                .build();
//
//        List<Document> docs = vectorStore.similaritySearch(searchRequest);
//        log.info("Antal dokumenter fundet: " + docs.size());
//
//        StringBuilder contextBuilder = new StringBuilder();
//        for (Document doc : docs) {
//            log.info("Dokument indhold: " + doc.getText());
//            contextBuilder.append(doc.getText()).append("\n");
//        }
//
//        // Byg det endelige prompt med kontekst og spørgsmål
//        String context = docs.stream()
//                .map(Document::getText)
//                .collect(Collectors.joining("\n"));
//
//        String finalPrompt = """
//Du er en dansk politiker, der svarer en borger. Du svarer altid på dansk.
//Svar kun baseret på den følgende information. Hvis du ikke kan finde svaret, så sig: "Det ved jeg ikke."
//
//Baggrundsinformation:
//%s
//
//Spørgsmål fra borgeren:
//%s
//""".formatted(context, userMessage);
//
//        log.debug("Final Prompt til LLM:\n{}", finalPrompt);
//        System.out.println("========= FINAL PROMPT =========\n" + finalPrompt);
//
//        System.out.println("========= DEBUG OUTPUT =========");
//        System.out.println("Context used:\n" + context);
//        System.out.println("User question:\n" + userMessage);
//
//        // Kald den lokale LLM (uden QuestionAnswerAdvisor)
//        return chatClient
//                .prompt(finalPrompt)
//                .advisors(
//                        new MessageChatMemoryAdvisor(chatMemory) // Vi beholder memory advisor
//                )
//                .call()
//                .content();
//    }


