package ua.hudyma.userservice.graphcontroller;

import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import ua.hudyma.userservice.client.RideClient;
import ua.hudyma.userservice.domain.Driver;
import ua.hudyma.userservice.repository.DriverRepository;
import ua.hudyma.userservice.repository.UserRepository;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class DriverController {
    private final DriverRepository driverRepository;
    private final UserRepository userRepository;
    private final RideClient rideClient;

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
    public Driver createDriver (@Argument Long vehicleId,
                                @Argument String licenseNumber,
                                @Argument String userId) {
        var user = userRepository.findById(userId).orElseThrow();
        var driver = new Driver();
        if (!rideClient.vehicleExists(vehicleId)) {
            throw new IllegalArgumentException("vehicle DOES not exist");
        }
        driver.setVehicleId(vehicleId);
        driver.setLicenseNumber(licenseNumber);
        driver.setUserId(userId);
        driverRepository.save(driver);
        user.setDriverId(driver.getId());
        userRepository.save(user);
        return driver;
    }

}
