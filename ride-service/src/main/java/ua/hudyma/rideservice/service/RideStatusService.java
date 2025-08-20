package ua.hudyma.rideservice.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ua.hudyma.rideservice.domain.Ride;

import static ua.hudyma.rideservice.constants.RideStatus.PAX_ONBOARD;

@Service
@RequiredArgsConstructor
@Log4j2
public class RideStatusService {
    @PersistenceContext
    private final EntityManager em;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    void setRideStatusOnBoardAndFlush(Long rideId) {
        var ride = em.find(Ride.class, rideId);
        ride.setRideStatus(PAX_ONBOARD);
        em.flush();
    }


}
