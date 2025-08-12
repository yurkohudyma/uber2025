package ua.hudyma.domain;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("users")
@Data
@AllArgsConstructor
@NoArgsConstructor

public class User {
    @Id
    private String id;
    private String email;
    private String driverId;
    private String paxId;
}
