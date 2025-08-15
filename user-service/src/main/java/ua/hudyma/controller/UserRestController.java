package ua.hudyma.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ua.hudyma.repository.UserRepository;
import ua.hudyma.service.UserService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserRestController {
    private final UserService userService;
    private final UserRepository userRepository;

    @GetMapping("/paxExists")
    public boolean existsUserByPaxId (@RequestParam String paxId){
        return userService.existsByPaxId(paxId);
    }

    @GetMapping("/driverExists")
    public boolean existsUserByDriverId (@RequestParam String driverId){
        return userService.existsByDriverId(driverId);
    }

    @DeleteMapping("/{userId}")
    public void deleteUser (@PathVariable String userId){
        userRepository.findById(userId)
                .ifPresent(userRepository::delete);
    }


}
