package ua.hudyma.rideservice.exception;

public class RideAllreadyAcceptedException extends RuntimeException {
    public RideAllreadyAcceptedException(String message) {
        super(message);
    }
}
