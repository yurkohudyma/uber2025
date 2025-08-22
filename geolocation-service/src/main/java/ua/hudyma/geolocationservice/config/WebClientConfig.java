package ua.hudyma.geolocationservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Bean
    public WebClient userServiceWebClient(WebClient.Builder builder) {
        return builder
                .baseUrl("http://localhost:9091").build();
    }

    @Bean
    public WebClient rideServiceWebClient(WebClient.Builder builder) {
        return builder
                .baseUrl("http://localhost:9097").build();
    }
}

