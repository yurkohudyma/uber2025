package ua.hudyma.ratingservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import ua.hudyma.ratingservice.client.RideClient;
import ua.hudyma.ratingservice.domain.Review;
import ua.hudyma.ratingservice.domain.UserReviewStats;
import ua.hudyma.ratingservice.dto.ReviewRequestDto;
import ua.hudyma.ratingservice.dto.RideResponseDto;
import ua.hudyma.ratingservice.enums.Rating;
import ua.hudyma.ratingservice.enums.ReviewAuthor;
import ua.hudyma.ratingservice.exception.ReviewAllreadyExistsException;
import ua.hudyma.ratingservice.repository.ReviewRepository;
import ua.hudyma.ratingservice.repository.UserReviewStatsRepository;

import java.util.List;

import static ua.hudyma.ratingservice.enums.ReviewAuthor.*;

@Service
@RequiredArgsConstructor
@Log4j2
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final UserReviewStatsRepository userReviewStatsRepository;
    private final RideClient rideClient;

    public Review addReview(ReviewRequestDto requestDto) {
        var rideResponseDto = rideClient.getRideDto(requestDto.rideId());
        var reviewAuthor = requestDto.reviewAuthor();
        var forUserId = reviewAuthor == PAX
                ? rideResponseDto.driverId() : rideResponseDto.paxId();
        var fromUserId = reviewAuthor == DRIVER
                ? rideResponseDto.driverId() : rideResponseDto.paxId();

        if (reviewRepository.existsByForUserId(forUserId)){
            throw new ReviewAllreadyExistsException("Review already exists for userId = " + forUserId);
        }
        if (reviewRepository.existsByFromUserId(fromUserId)){
            throw new ReviewAllreadyExistsException("Review already exists for userId = " + fromUserId);
        }

        var review = new Review();
        review.setRating(requestDto.rating());
        review.setFeedback(requestDto.feedback());
        review.setForUserId(forUserId);
        review.setFromUserId(fromUserId);
        review.setReviewAuthor(reviewAuthor);
        review.setRideId(requestDto.rideId());

        if (requestDto.comment() != null){
            review.setComment(requestDto.comment());
        }
        reviewRepository.save(review);
        var userReviewStats = userReviewStatsRepository
                .findByUserId(forUserId);
        if (userReviewStats.isPresent()){
            updateUserReviewStats(userReviewStats.get(), forUserId);
        }
        else {
            var newUserReviewStats = new UserReviewStats();
            newUserReviewStats.setUserId(forUserId);
            newUserReviewStats.setAverageRating(requestDto.rating().getStars());
            newUserReviewStats.setPublishedReviewsNumber(1L);
            userReviewStatsRepository.save(newUserReviewStats);
        }
        return review;
    }

    private void updateUserReviewStats(
            UserReviewStats userReviewStats,
            String userId) {
        var userReviews = reviewRepository
                .findByForUserId(userId);
        var averageRating = userReviews
                .stream()
                .map(Review::getRating)
                .mapToDouble(Rating::getStars)
                .average()
                .orElseGet(() ->
                        userReviews
                                .get(0)
                                .getRating()
                                .getStars());
        userReviewStats.setPublishedReviewsNumber(userReviewStats
                .getPublishedReviewsNumber() + 1);
        userReviewStats.setAverageRating(averageRating);
        userReviewStats.setPublishedReviewsNumber(userReviews.size() + 1L);
        userReviewStats.setUserId(userId);
        userReviewStatsRepository.save(userReviewStats);
    }

    public List<Review> getAll() {
        return reviewRepository.findAll();
    }

    public List<Review> getAllFromUser(String fromUserId) {
        return reviewRepository.findByFromUserId(fromUserId);
    }

    public RideResponseDto getRide(Long rideId) {
        return rideClient.getRideDto(rideId);
    }
}
