package ua.hudyma.dto;
//todo refuck package into proprietory

import ua.hudyma.enums.TrackDirection;
import java.util.List;

public record RouteDto(
        RoutePoint departure,
        RoutePoint destination,
        List<RoutePoint> transitPointsList,
        TrackDirection trackDirection) {
}
