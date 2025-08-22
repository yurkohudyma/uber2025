package ua.hudyma.userservice.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import ua.hudyma.userservice.domain.Driver;

public interface DriverRepository extends MongoRepository<Driver, String> {
}
