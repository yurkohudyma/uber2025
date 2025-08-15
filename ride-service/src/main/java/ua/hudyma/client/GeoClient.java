package ua.hudyma.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import ua.hudyma.dto.RouteDistanceResponseDto;
import ua.hudyma.dto.RouteDto;

@Service
@RequiredArgsConstructor
@Log4j2
public class GeoClient {
    private final WebClient geoServiceWebClient;

    public RouteDistanceResponseDto getDistanceWithTrack(RouteDto dto){
        return geoServiceWebClient
                .post()
                .uri("/distance")
                .bodyValue(dto)
                .retrieve()
                .bodyToMono(RouteDistanceResponseDto.class)
                .block();
    }

    public RouteDistanceResponseDto getDistance(RouteDto dto){
        return geoServiceWebClient
                .post()
                .uri("/distance/noTrack")
                .bodyValue(dto)
                .retrieve()
                .bodyToMono(RouteDistanceResponseDto.class)
                .block();
    }
}
