package ua.hudyma.ratingservice.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import ua.hudyma.ratingservice.enums.Rating;
import ua.hudyma.ratingservice.enums.ReviewAuthor;

import java.util.Date;

@Entity
@Table(name = "reviews")
@Data
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    Long rideId;
    @Column(nullable = false)
    String forUserId;
    @Column(nullable = false)
    String fromUserId;
    @JsonFormat(pattern = "dd-MM-yyyy HH:mm")
    @CreationTimestamp
    private Date publishedOn;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Rating rating;
    @Column(nullable = false)
    private String feedback;
    private String comment;
    @Enumerated(value = EnumType.STRING)
    @Column(nullable = false)
    private ReviewAuthor reviewAuthor;

}
