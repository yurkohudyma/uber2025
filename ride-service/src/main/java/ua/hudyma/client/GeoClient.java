package ua.hudyma.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import ua.hudyma.dto.RouteDistanceResponseDto;
import ua.hudyma.dto.RouteDto;

@Service
@RequiredArgsConstructor
@Log4j2
public class GeoClient {
    private final WebClient geoServiceWebClient;

    public Flux<RouteDistanceResponseDto> getDistance (RouteDto dto){
        return geoServiceWebClient
                .post()
                .uri("/distance")
                .bodyValue(dto)
                .retrieve()
                .bodyToFlux(RouteDistanceResponseDto.class);
    }
}
