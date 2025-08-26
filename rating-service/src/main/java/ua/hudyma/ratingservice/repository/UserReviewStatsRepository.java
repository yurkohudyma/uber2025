package ua.hudyma.ratingservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ua.hudyma.ratingservice.domain.UserReviewStats;

import java.util.Optional;

@Repository public interface UserReviewStatsRepository extends JpaRepository<UserReviewStats, Long> {
    Optional<UserReviewStats> findByUserId(String userId);
}
