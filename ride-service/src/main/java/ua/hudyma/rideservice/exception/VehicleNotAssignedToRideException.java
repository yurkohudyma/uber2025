package ua.hudyma.rideservice.exception;

public class VehicleNotAssignedToRideException extends RuntimeException {
    public VehicleNotAssignedToRideException(String message) {
        super(message);
    }
}
