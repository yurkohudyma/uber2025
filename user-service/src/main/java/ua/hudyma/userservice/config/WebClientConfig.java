package ua.hudyma.userservice.config;

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
    public WebClient rideServiceWebClient(Builder builder) {
        return builder
                .baseUrl("http://localhost:9097").build();
    }
}



