package ua.hudyma.ratingservice.dto;

import ua.hudyma.ratingservice.enums.Rating;
import ua.hudyma.ratingservice.enums.ReviewAuthor;

public record ReviewRequestDto(
        ReviewAuthor reviewAuthor,
        Long rideId,
        Rating rating,
        String feedback,
        String comment) {
}
