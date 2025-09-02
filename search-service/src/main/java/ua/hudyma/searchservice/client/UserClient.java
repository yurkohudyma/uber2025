package ua.hudyma.searchservice.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@RequiredArgsConstructor
@Log4j2
public class UserClient {

    private final WebClient userServiceWebClient;

    public boolean paxExists(String paxId) {
        return Boolean.TRUE.equals(userServiceWebClient
                .get()
                .uri("/users/paxExists?paxId={paxId}", paxId)
                .retrieve()
                .bodyToMono(Boolean.class)
                .block());
    }

    public boolean driverExists(String driverId) {
        return Boolean.TRUE.equals(userServiceWebClient
                .get()
                .uri("/users/driverExists?driverId={driverId}", driverId)
                .retrieve()
                .bodyToMono(Boolean.class)
                .block());
    }
}
