package ua.hudyma.userservice.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ua.hudyma.userservice.repository.PaxRepository;

@RestController
@RequestMapping("/pax")
@RequiredArgsConstructor
@Log4j2
public class PaxRestController {

    private final PaxRepository paxRepository;

    @DeleteMapping("/all")
    public void deleteAllPax (){
        var allPax = paxRepository.findAll();
        paxRepository.deleteAll(allPax);
    }
}
