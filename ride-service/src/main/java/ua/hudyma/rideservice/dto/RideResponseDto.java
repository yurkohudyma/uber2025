package ua.hudyma.rideservice.dto;

public record RideResponseDto(
        Long rideId,
        String driverId,
        String paxId) {}
