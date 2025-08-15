package ua.hudyma.service;


import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import ua.hudyma.dto.RouteDistanceResponseDto;
import ua.hudyma.dto.RouteDto;

import java.util.ArrayList;
import java.util.List;

import static ua.hudyma.service.GpxWriter.writeGpxFile;

@Component
@ConfigurationProperties(prefix = "graphhopper.api")
public class DistanceService {

    private static final RestTemplate restTemplate = new RestTemplate();
    private static String staticKey;
    @Value("${graphhopper.api.key}")
    private String key;

    void setKey(String key) {
        this.key = key;
        DistanceService.staticKey = key;
    }

    static String getStaticKey() {
        return staticKey;
    }

    public static RouteDistanceResponseDto getDistance(
            RouteDto routeDto, boolean provideTrack) {
        String key = getStaticKey();

        var departure = routeDto.departure();
        var destination = routeDto.destination();
        String url = UriComponentsBuilder.fromHttpUrl("https://graphhopper.com/api/1/route")
                .queryParam("point", departure.latitude() + "," + departure.longitude())
                .queryParam("point", destination.latitude() + "," + destination.longitude())
                .queryParam("vehicle", "car")
                .queryParam("locale", "uk")
                .queryParam("points_encoded", "false")
                .queryParam("calc_points", "true")
                .queryParam("key", key)
                //.queryParam("type", "gpx")
                .toUriString();

        var response = restTemplate.getForEntity(url, JsonNode.class);
        double distanceInMeters = response.getBody()
                .get("paths")
                .get(0)
                .get("distance")
                .asDouble();

        var points = response.getBody()
                .get("paths")
                .get(0)
                .get("points")
                .get("coordinates");

        List<double[]> routePointsCoordsList = new ArrayList<>();
        for (JsonNode point : points) {
            double lon = point.get(0).asDouble();
            double lat = point.get(1).asDouble();
            routePointsCoordsList.add(new double[]{lat, lon});
        }
        writeGpxFile(routePointsCoordsList);

        return provideTrack ? new RouteDistanceResponseDto(
                distanceInMeters/1000,
                routePointsCoordsList):
                new RouteDistanceResponseDto(
                        distanceInMeters/1000, null);
    }
}
