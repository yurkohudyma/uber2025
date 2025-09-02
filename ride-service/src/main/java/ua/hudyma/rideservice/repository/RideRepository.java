package ua.hudyma.rideservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ua.hudyma.rideservice.domain.Ride;

import java.util.List;

@Repository
public interface RideRepository extends JpaRepository<Ride, Long> {
    @Query(value = """
    SELECT * FROM rides
    WHERE (driver_id = :userId OR pax_id = :userId)
      AND (LOWER(pax_id) LIKE LOWER(CONCAT('%', :str, '%')) OR
        LOWER(driver_id) LIKE LOWER(CONCAT('%', :str, '%'))
      )
    """, nativeQuery = true)
    List<Ride> searchRidesByUserIdAndText(
            @Param("str") String str,
            @Param("userId") String userId
    );

}
