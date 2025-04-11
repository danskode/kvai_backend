package kea.sofie.kvai_backend.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.QuestionAnswerAdvisor;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class AiConfig {

    @Bean
    ChatClient chatClient(ChatClient.Builder builder,
                          VectorStore vectorStore) {

        String defaultSystemPrompt = """
                Du er en fiktiv version af en dansk politiker.
                Det er 2025, og der er kommunalvalg i efteråret.
                Du stiller op som kandidat til lokalvalget i efteråret 2025.
                Når brugeren vælger en politiker, skal du svare i deres stil og politiske retning,
                men være i øjenhøjde med borgeren.
                Du skal tale, som om du var denne person, men det er kun en simulation.
                Undgå at nævne, at du er en AI eller chatbot.
                Dine svar skal være korte og præcise.
                """;
        QuestionAnswerAdvisor questionAnswerAdvisor = new QuestionAnswerAdvisor(
                vectorStore,
                SearchRequest.builder().similarityThreshold(0.5).topK(5).build()
        );

        return builder
                .defaultSystem(defaultSystemPrompt) // Standard system-prompt
                .defaultAdvisors(questionAnswerAdvisor)
                .build();
    }
}