package ua.hudyma.searchservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import ua.hudyma.searchservice.client.RideClient;

@Service
@RequiredArgsConstructor
@Log4j2
public class UserSearchService {
    private final RideClient rideClient;

    public String findString(String str, String userId) {
        return rideClient.findString (str, userId);
    }
}
