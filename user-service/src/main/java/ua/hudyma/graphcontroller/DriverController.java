package ua.hudyma.graphcontroller;

import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import ua.hudyma.domain.Driver;
import ua.hudyma.repository.DriverRepository;
import ua.hudyma.repository.UserRepository;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class DriverController {
    private final DriverRepository driverRepository;
    private final UserRepository userRepository;

    @QueryMapping
    public Driver driver (@Argument String id){
        return driverRepository.findById(id).orElse(null);
    }

    @QueryMapping
    public List<Driver> drivers (){
        return driverRepository.findAll();
    }

    @MutationMapping
    //@Transactional :: needs to relaunch mongo-server in replica-set mode, which supports TX
    public Driver createDriver (@Argument String carId,
                                @Argument String licenseNumber,
                                @Argument String userId){
        var user = userRepository.findById(userId).orElseThrow();
        var driver = new Driver();
        driver.setCarId(carId);
        driver.setLicenseNumber(licenseNumber);
        driver.setUserId(userId);
        driverRepository.save(driver);
        user.setDriverId(driver.getId());
        userRepository.save(user);
        return driver;
    }
}
