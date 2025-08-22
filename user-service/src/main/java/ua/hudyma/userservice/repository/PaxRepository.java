package ua.hudyma.userservice.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import ua.hudyma.userservice.domain.Pax;

public interface PaxRepository extends MongoRepository<Pax, String> {
}
