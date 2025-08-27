package ua.hudyma.ratingservice.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ua.hudyma.ratingservice.domain.Review;
import ua.hudyma.ratingservice.dto.ReviewRequestDto;
import ua.hudyma.ratingservice.dto.RideResponseDto;
import ua.hudyma.ratingservice.service.ReviewService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/reviews")
@Log4j2 public class ReviewController {
    private final ReviewService reviewService;

    @GetMapping("/getRide/{rideId}")
    public ResponseEntity<RideResponseDto> getRideDto (
            @PathVariable Long rideId){
        return ResponseEntity.ok(reviewService
                .getRide (rideId));
    }

    @GetMapping
    public List<Review> getAll (){
        return reviewService.getAll();
    }

    @GetMapping("/{fromUserId}")
    public List<Review> getAllReviewsFromUser (
            @PathVariable String fromUserId){
        return reviewService.getAllFromUser(fromUserId);
    }

    @PostMapping
    public ResponseEntity<Review> addReview (
            @RequestBody ReviewRequestDto requestDto){
        return ResponseEntity.ok(reviewService
                .addReview (requestDto));
    }


}
