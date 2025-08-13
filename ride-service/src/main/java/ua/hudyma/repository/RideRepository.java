package ua.hudyma.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ua.hudyma.domain.Ride;

@Repository
public interface RideRepository extends JpaRepository<Ride, Long> {
}
