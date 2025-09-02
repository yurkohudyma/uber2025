package ua.hudyma.searchservice.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@RequiredArgsConstructor
@Log4j2
public class RatingClient {

    public final WebClient ratingServiceWebClient;

    /*public boolean vehicleExists (Long vehicleId){
        return Boolean.TRUE.equals(rideServiceWebClient
                .get()
                .uri("/rides/vehicleExists?vehicleId={vehicleId}", vehicleId)
                .retrieve()
                .bodyToMono(Boolean.class)
                .block());
    }*/
}
