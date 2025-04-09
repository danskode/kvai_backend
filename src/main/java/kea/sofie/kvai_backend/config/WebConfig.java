package kea.sofie.kvai_backend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    // sikrer at backend og frontend kan kommunikere sammen.....
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/chat") // endpoint i controlleren
                .allowedOrigins("http://localhost:63342")   // URL til frontend
                .allowCredentials(true)  // det her tilllader deling af cookies... vigtigt..
                .allowedMethods("GET", "POST", "PUT", "DELETE");
    }
}