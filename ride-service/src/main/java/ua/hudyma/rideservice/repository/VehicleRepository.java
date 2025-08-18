package ua.hudyma.rideservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ua.hudyma.rideservice.domain.Vehicle;

@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, Long> {
}
