package ua.hudyma.userservice.domain;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("pax")
@Data
public class Pax {

    @Id
    private String id;
    private String userId;
    private String city;


}
