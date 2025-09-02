package ua.hudyma.rideservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import ua.hudyma.rideservice.repository.RideRepository;

@Service
@RequiredArgsConstructor
@Log4j2
public class SearchService {

    private final RideRepository rideRepository;

    public String findStr(String str) {
        return rideRepository.findString (str);
    }
}
