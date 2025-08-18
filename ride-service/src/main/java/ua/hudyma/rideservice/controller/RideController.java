package ua.hudyma.rideservice.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ua.hudyma.rideservice.domain.Ride;
import ua.hudyma.rideservice.dto.RideRequestDto;
import ua.hudyma.rideservice.dto.RouteDistanceResponseDto;
import ua.hudyma.rideservice.dto.RouteDto;
import ua.hudyma.rideservice.dto.RoutePoint;
import ua.hudyma.rideservice.enums.TrackDirection;
import ua.hudyma.rideservice.repository.RideRepository;
import ua.hudyma.rideservice.repository.VehicleRepository;
import ua.hudyma.rideservice.service.RideService;

@RestController
@RequestMapping("/rides")
@Log4j2
@RequiredArgsConstructor

public class RideController {

    private final RideService rideService;
    private final VehicleRepository vehicleRepository;
    private final RideRepository rideRepository;

    @PostMapping
    public ResponseEntity<Ride> addRide (
            @RequestBody RideRequestDto dto){
        return ResponseEntity.ok(
                rideService.addRide (dto));
    }

    @GetMapping("/paxExists")
    public boolean userExistsByPaxId (
            @RequestParam String paxId){
        return rideService.existsByPaxId(paxId);
    }

    @GetMapping("/driverExists")
    public boolean userExistsDriveByDriverId (
            @RequestParam String driverId){
        return rideService.existsByDriverId(driverId);
    }

    @PostMapping("/distance")
    public ResponseEntity<RouteDistanceResponseDto> getDistanceWithTrack
            (@RequestBody RouteDto dto){
        var result = rideService
                .getDistance(dto, true);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/distance/noTrack")
    public ResponseEntity<RouteDistanceResponseDto> getDistance
            (@RequestBody RouteDto dto){
        var result = rideService
                .getDistance(dto, false);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/launchToPaxRoute")
    public ResponseEntity<Boolean> launchToPaxRoute (
            @RequestBody RideRequestDto dto){
        return ResponseEntity.ok(rideService
                .acceptRideByDriver(dto));
    }

    @PostMapping("/declineRide")
    public ResponseEntity<Boolean> declineRideByDriver (
            @RequestBody RideRequestDto dto){
        return ResponseEntity.ok(rideService
                .declineAcceptedRideByDriver(dto));
    }

    @GetMapping("/toPAX")
    public ResponseEntity<RouteDistanceResponseDto> getDistanceWithTrack
            (@RequestParam Long rideId){
        var ride = rideRepository
                .findById(rideId).orElseThrow(
                        () -> new IllegalArgumentException
                                ("Ride has NOT BEEN FOUND"));
        var currentPosition = ride
                .getVehicle().getCurrentPosition();
        if (currentPosition == null){
            throw new IllegalArgumentException
                    (" --> vehicle cur position is NA");
        }
        var dto = new RouteDto(
                currentPosition,
                ride.getDeparture(),
                TrackDirection.toPAX);
        var result = rideService
                .getDistance(dto, true);
        return ResponseEntity.ok(result);
    }
}
