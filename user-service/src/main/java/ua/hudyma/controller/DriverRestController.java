package ua.hudyma.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ua.hudyma.repository.DriverRepository;

@RestController
@RequestMapping("/drivers")
@RequiredArgsConstructor
@Log4j2
public class DriverRestController {

    private final DriverRepository driverRepository;

    @DeleteMapping("/all")
    public void deleteAllDrivers (){
        var allDrivers = driverRepository.findAll();
        driverRepository.deleteAll(allDrivers);
    }
}
