package ua.hudyma.userservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ua.hudyma.userservice.domain.User;
import ua.hudyma.userservice.repository.UserRepository;
import ua.hudyma.userservice.service.UserService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserRestController {
    private final UserService userService;
    private final UserRepository userRepository;

    @GetMapping
    public ResponseEntity<String> searchUserByEmail (
            @RequestParam String email){
        return ResponseEntity.ok(
                userRepository
                        .findByEmail(email)
                        .orElseThrow()
                        .getId());
    }

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
