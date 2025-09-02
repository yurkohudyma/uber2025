package ua.hudyma.userservice.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import ua.hudyma.userservice.domain.User;

import java.util.Optional;

public interface UserRepository extends MongoRepository<User, String> {

    boolean existsByDriverId(String driverId);

    boolean existsByPaxId(String paxId);

    Optional<User> findByEmail(String email);
}
