package ua.hudyma.rideservice.exception;

public class RideNotAcceptedException extends RuntimeException {
    public RideNotAcceptedException(String message) {
        super(message);
    }
}
