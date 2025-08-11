package ua.hudyma.domain;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "drivers")
@Data
public class Driver {

    @Id
    private Long id;
    private String userId;
    private String licenseNumber;
    private String carId;
}
