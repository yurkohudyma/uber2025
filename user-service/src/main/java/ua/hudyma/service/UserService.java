package ua.hudyma.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import ua.hudyma.repository.UserRepository;

@Service
@RequiredArgsConstructor
@Log4j2
public class UserService {
    private final UserRepository userRepository;

    public boolean existsByDriverId(String driverId) {
        return userRepository.existsByDriverId(driverId);
    }

    public boolean existsByPaxId(String paxId) {
        return userRepository.existsByPaxId(paxId);
    }
}
