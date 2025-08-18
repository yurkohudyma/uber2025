package ua.hudyma.rideservice.dto;

import ua.hudyma.rideservice.enums.TrackDirection;

public record RouteDto(RoutePoint departure,
                       RoutePoint destination,
                       TrackDirection trackDirection) {
}
