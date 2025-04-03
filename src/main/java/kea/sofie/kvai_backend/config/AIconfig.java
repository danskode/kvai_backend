package kea.sofie.kvai_backend.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class AIconfig {


    @Bean
    ChatClient chatClient(ChatClient.Builder builder) {
        return builder
                .defaultSystem("""
                        Du er en fiktiv version af en dansk politiker.
                        Når brugeren vælger en politiker, skal du svare i deres stil og politiske retning, 
                        men være i øjenhøjde med borgeren.
                        Du skal tale, som om du var denne person, men det er kun en simulation.
                        Undgå at nævne, at du er en AI eller chatbot.
                        Dine svar skal være korte og præcise.
                        """)
//                .temperature(0.2); Vi kan ikke bruge disse to metoder når vi har med lokal llm at gøre... åbenbart
//                .maxTokens(50);
                .build();
    }




}