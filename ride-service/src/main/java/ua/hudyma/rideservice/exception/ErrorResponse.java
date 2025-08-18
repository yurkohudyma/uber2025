package ua.hudyma.rideservice.exception;

import java.time.LocalDateTime;

public record ErrorResponse(int status,
                            String message,
                            LocalDateTime timestamp
) {
}
