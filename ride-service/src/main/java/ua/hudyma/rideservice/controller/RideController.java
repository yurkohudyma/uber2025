package ua.hudyma.rideservice.controller;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ua.hudyma.rideservice.domain.Ride;
import ua.hudyma.rideservice.dto.*;
import ua.hudyma.rideservice.constants.RideStatus;
import ua.hudyma.rideservice.constants.TrackDirection;
import ua.hudyma.rideservice.repository.RideRepository;
import ua.hudyma.rideservice.repository.VehicleRepository;
import ua.hudyma.rideservice.service.RideService;

import java.util.List;

@RestController
@RequestMapping("/rides")
@Log4j2
@RequiredArgsConstructor

public class RideController {

    private final RideService rideService;
    private final VehicleRepository vehicleRepository;
    private final RideRepository rideRepository;

    @PostMapping
    public ResponseEntity<Ride> addRide(
            @RequestBody RideRequestDto dto) {
        return ResponseEntity.ok(
                rideService.addRide(dto));
    }

    @GetMapping("/paxExists")
    public boolean userExistsByPaxId(
            @RequestParam String paxId) {
        return rideService.existsByPaxId(paxId);
    }

    @GetMapping("/driverExists")
    public boolean userExistsDriveByDriverId(
            @RequestParam String driverId) {
        return rideService.existsByDriverId(driverId);
    }

    @PostMapping("/distance")
    public ResponseEntity<RouteDistanceResponseDto> getDistanceWithTrack
            (@RequestBody RouteDto dto) {
        var result = rideService
                .getDistance(dto, true);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/distance/noTrack")
    public ResponseEntity<RouteDistanceResponseDto> getDistance
            (@RequestBody RouteDto dto) {
        var result = rideService
                .getDistance(dto, false);
        log.info(" --> отримане ДТО: {}", dto);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/distanceForMap")
    public double getDistanceMap
            (@RequestBody RouteDto dto) {
        return rideService
                .getDistanceMap(dto);
    }

    @PostMapping("/initTransfer")
    public void initPaxTransfer(@RequestBody RideRequestDto dto) {
        rideService.initTransfer(dto);
    }


    @PostMapping("/launchToPaxRoute")
    public ResponseEntity<RideStatus> launchToPaxRoute(
            @RequestBody RideRequestDto dto) {
        return ResponseEntity.ok(rideService
                .acceptRideByDriver(dto));
    }

    @GetMapping("/getStatus")
    public RideStatus getRideStatus (@RequestParam Long rideId){
        var ride = rideRepository
                .findById(rideId)
                .orElseThrow(
                        () ->
                                new IllegalArgumentException(
                                        "Ride has NOT BEEN FOUND"));
        return ride.getRideStatus();
    }


    @PostMapping("/declineRide")
    public ResponseEntity<RideStatus> declineRideByDriver(
            @RequestBody RideRequestDto dto) {
        return ResponseEntity.ok(rideService
                .declineAcceptedRideByDriver(dto));
    }

    @GetMapping("/toPAX")
    public ResponseEntity<RouteDistanceResponseDto>
    getDistanceWithTrack
            (@RequestParam Long rideId) {
        var ride = rideRepository
                .findById(rideId).orElseThrow(
                        () -> new IllegalArgumentException
                                ("Ride has NOT BEEN FOUND"));
        var currentPosition = ride
                .getVehicle().getCurrentPosition();
        if (currentPosition == null) {
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

    @GetMapping("/map")
    public MapInfoDto getMapInfo(@RequestParam Long rideId) {
        var ride = rideRepository
                .findById(rideId).orElseThrow(
                        () ->
                                new IllegalArgumentException
                                        ("Ride has NOT BEEN FOUND"));
        var currentPosition = ride
                .getVehicle().getCurrentPosition();
        if (currentPosition == null) {
            throw new IllegalArgumentException
                    (" --> vehicle cur position is NA");
        }
        var vehicleCurrentPosition = List
                .of(currentPosition.latitude(),
                        currentPosition.longitude());
        var departure =
                List.of(
                        ride.getDeparture().latitude(),
                        ride.getDeparture().longitude());
        var destination = List.of(
                ride.getDestination().latitude(),
                ride.getDestination().longitude());
        var toPAXrouteDto = new RouteDto(
                currentPosition,
                ride.getDeparture(),
                TrackDirection.toPAX);

        var toPAXroute = rideService
                .getDistance(toPAXrouteDto, true)
                .routePoints()
                .stream()
                .map(point -> List.of(point[0], point[1]))
                .toList();

        var routeDto = new RouteDto(
                ride.getDeparture(),
                ride.getDestination(),
                TrackDirection.ROUTE);
        var route = rideService
                .getDistance(routeDto, true)
                .routePoints()
                .stream()
                .map(point -> List.of(point[0], point[1]))
                .toList();
        return new MapInfoDto(
                vehicleCurrentPosition,
                departure,
                destination,
                toPAXroute,
                route);
    }

    @GetMapping("/getPosition")
    public List<Double> updateVehicleCurPos(@RequestParam Long vehicleId) {
        var vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new IllegalArgumentException
                        ("Vehicle has NOT BEEN FOUND"));
        return List.of(vehicle.getCurrentPosition().latitude(),
                vehicle.getCurrentPosition().longitude());
    }
}