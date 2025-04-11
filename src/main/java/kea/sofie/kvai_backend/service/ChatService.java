package kea.sofie.kvai_backend.service;

import kea.sofie.kvai_backend.model.Politician;
import kea.sofie.kvai_backend.repository.PoliticianRepository;
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
import java.util.Map;

@Service
public class ChatService {

    private static final Logger log = LoggerFactory.getLogger(ChatService.class);
    private ChatClient chatClient;
    private final VectorStore vectorStore;
    private final PoliticianRepository politicianRepository;

    @Autowired
    public ChatService(ChatClient chatClient,
                       VectorStore vectorStore,
                       PoliticianRepository politicianRepository) {
        this.chatClient = chatClient;
        this.vectorStore = vectorStore;
        this.politicianRepository = politicianRepository;
    }

    public List<Politician> getPoliticians() {
        return politicianRepository.findAll();
    }

    private String getPromptForPolitician(String name) {
        Politician politician = politicianRepository.findByName(name);
        if (politician == null) {
            return "Kunne ikke finde en politiker med navnet: " + name;
        } else {
            return
                    "Du er en fiktiv version af politikeren: " +
                            politician.getName() +
                            " fra " +
                            politician.getArea() +
                            ", medlem af " +
                            politician.getParty() +
                            ".";
        }
    }

    private String buildSystemPrompt(String politicianPrompt) {
        return politicianPrompt +
                "\n\nSvar kort og præcist, men med brugeren i øjenhøjde." +
                "\nUndgå at nævne at du er en chatbot.";
    }


    public String getAIResponse(String userMessage,
                                String politicianName,
                                ChatMemory chatMemory) {

        Politician selectedPolitician = politicianRepository.findByName(politicianName);

        if(selectedPolitician == null) {
            return "Beklager, kunne ikke finde den valgte politiker...";
        }

        String politicianPrompt = getPromptForPolitician(selectedPolitician.getName());
        String fullPrompt = buildSystemPrompt(politicianPrompt);

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


