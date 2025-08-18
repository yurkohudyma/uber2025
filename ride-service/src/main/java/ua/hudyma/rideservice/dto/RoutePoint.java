package ua.hudyma.rideservice.dto;

import jakarta.persistence.Embeddable;

@Embeddable
public record RoutePoint(double latitude, double longitude) {
}
