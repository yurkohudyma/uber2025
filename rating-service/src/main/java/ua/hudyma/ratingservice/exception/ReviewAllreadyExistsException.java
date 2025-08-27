package ua.hudyma.ratingservice.exception;

public class ReviewAllreadyExistsException extends RuntimeException {

    public ReviewAllreadyExistsException(String message) {
        super(message);
    }
}
