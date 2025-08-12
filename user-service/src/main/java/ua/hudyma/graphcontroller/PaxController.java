package ua.hudyma.graphcontroller;

import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import ua.hudyma.domain.Pax;
import ua.hudyma.domain.User;
import ua.hudyma.repository.PaxRepository;
import ua.hudyma.repository.UserRepository;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class PaxController {

    private final UserRepository userRepository;
    private final PaxRepository paxRepository;

    @QueryMapping
    public Pax pax(@Argument String id) {
        return paxRepository.findById(id).orElse(null);
    }

    @QueryMapping
    public List<Pax> paxx() {
        return paxRepository.findAll();
    }

    @MutationMapping
    public Pax createPax(@Argument String city,
                         @Argument String userId) {
        var pax = new Pax();
        pax.setCity(city);
        var user = userRepository.findById(userId).orElseThrow();
        pax.setUserId(userId);
        paxRepository.save(pax);
        user.setPaxId(pax.getId());
        userRepository.save(user);
        return pax;
    }
}

