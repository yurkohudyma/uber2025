package ua.hudyma.ratingservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ua.hudyma.ratingservice.domain.Review;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    boolean existsByForUserId(String forUserId);

    boolean existsByFromUserId(String fromUserId);

    List<Review> findByForUserId(String userId);
     List<Review> findByFromUserId(String fromUseId);
}
