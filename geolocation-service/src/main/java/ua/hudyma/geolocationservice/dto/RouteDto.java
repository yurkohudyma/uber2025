package ua.hudyma.geolocationservice.dto;

import ua.hudyma.geolocationservice.enums.TrackDirection;
import java.util.List;

public record RouteDto(
        RoutePoint departure,
        RoutePoint destination,
        List<RoutePoint> transitPointsList,
        TrackDirection trackDirection) {
}
