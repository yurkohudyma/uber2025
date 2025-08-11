package ua.hudyma.graphcontroller;

import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.stereotype.Controller;
import ua.hudyma.domain.User;
import ua.hudyma.repository.UserRepository;

import java.util.List;

@Controller
public class UserGraphQLController {

    public UserGraphQLController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    private final UserRepository userRepository;

    @QueryMapping
    public User user(@Argument String id) {
        return userRepository.findById(id).orElse(null);
    }

    @QueryMapping
    public List<User> users() {
        return userRepository.findAll();
    }

    @MutationMapping
    public User createUser(@Argument String email) {
        User user = new User();
        user.setEmail(email);
        return userRepository.save(user);
    }
}

