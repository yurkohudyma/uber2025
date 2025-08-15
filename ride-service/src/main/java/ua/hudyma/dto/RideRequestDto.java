package ua.hudyma.dto;

import ua.hudyma.enums.VehicleClass;

public record RideRequestDto(RouteDto routeDto,
                             String paxId,
                             VehicleClass vehicleClass,
                             String driverId,
                             Long vehicleId,
                             Long rideId) {
}
