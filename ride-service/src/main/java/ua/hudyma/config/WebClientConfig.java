package ua.hudyma.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClient.Builder;

@Configuration
public class WebClientConfig {

    @Bean
    public WebClient geoServiceWebClient(Builder builder) {
        return builder.baseUrl("http://localhost:9094").build();
    }

    @Bean
    public WebClient userServiceWebClient(Builder builder) {
        return builder
                .baseUrl("http://localhost:9091").build();
    }
}



