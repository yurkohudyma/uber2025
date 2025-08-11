package ua.hudyma.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import ua.hudyma.domain.Pax;

public interface PaxRepository extends MongoRepository<Pax, String> {
}
