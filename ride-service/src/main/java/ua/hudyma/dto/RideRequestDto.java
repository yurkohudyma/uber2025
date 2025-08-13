package ua.hudyma.dto;

public record RideRequestDto(RoutePoint departure,
                             RoutePoint destination,
                             String driverId,
                             Long vehicleId,
                             String paxId) {
}
