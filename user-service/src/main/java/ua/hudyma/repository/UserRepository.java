package ua.hudyma.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import ua.hudyma.domain.User;

public interface UserRepository extends MongoRepository<User, String> {
}
