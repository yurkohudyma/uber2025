package ua.hudyma.rideservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import ua.hudyma.rideservice.domain.Ride;
import ua.hudyma.rideservice.repository.RideRepository;

import static java.util.stream.Collectors.joining;

@Service
@RequiredArgsConstructor
@Log4j2
public class SearchService {

    private final RideRepository rideRepository;

    public String findStr(String str, String userId) {
        return rideRepository.searchRidesByUserIdAndText(str, userId)
                .stream()
                .map(Ride::toString)
                .collect(joining(" "));

    }
}
