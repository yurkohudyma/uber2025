package ua.hudyma.rideservice.dto;

import ua.hudyma.rideservice.enums.VehicleClass;

public record RideRequestDto(RouteDto routeDto,
                             String paxId,
                             VehicleClass vehicleClass,
                             String driverId,
                             Long vehicleId,
                             Long rideId) {
}
