package ua.hudyma.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import ua.hudyma.domain.Driver;

public interface DriverRepository extends MongoRepository<Driver, String> {
}
