package ua.hudyma.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ua.hudyma.repository.UserRepository;
import ua.hudyma.service.UserService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserRestController {
    private final UserService userService;

    @GetMapping("/exists")
    public boolean existsUserByPaxId (@RequestParam String paxId){
        return userService.existsByPaxId(paxId);
    }


}
