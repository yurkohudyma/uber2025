package ua.hudyma.ratingservice.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.util.Date;

@Entity
@Table(name = "user_review_stats")
@Data
public class UserReviewStats {
    @Id
    @GeneratedValue(strategy =
            GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    String userId;
    @JsonFormat(pattern = "dd-MM-yyyy HH:mm")
    @CreationTimestamp
    private Date createdOn;
    @JsonFormat(pattern = "dd-MM-yyyy HH:mm")
    @UpdateTimestamp
    private Date updatedOn;
    @Positive
    Double averageRating;
    Long publishedReviewsNumber;
}
