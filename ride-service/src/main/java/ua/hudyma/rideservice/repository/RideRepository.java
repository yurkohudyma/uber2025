package ua.hudyma.rideservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ua.hudyma.rideservice.domain.Ride;

@Repository
public interface RideRepository extends JpaRepository<Ride, Long> {
}
