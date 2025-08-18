package ua.hudyma.rideservice.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;
import ua.hudyma.rideservice.dto.RoutePoint;
import ua.hudyma.rideservice.enums.VehicleClass;
import ua.hudyma.rideservice.enums.VehicleColor;
import ua.hudyma.rideservice.enums.VehicleModel;
import ua.hudyma.rideservice.enums.VehiclePropulsion;

import java.util.List;

@Entity
@Table(name = "vehicles")
@Data
public class Vehicle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String vehicleRegistrationNumber;

    @Enumerated(value = EnumType.STRING)
    @Column(nullable = false)
    private VehicleModel vehicleModel;
    private String modelMake;
    @Enumerated(value = EnumType.STRING)
    private VehicleColor vehicleColor;

    @Enumerated(value = EnumType.STRING)
    private VehiclePropulsion vehiclePropulsion;

    @Enumerated(value = EnumType.STRING)
    @Column(nullable = false)
    private VehicleClass vehicleClass;

    @OneToMany(mappedBy = "vehicle",
               fetch = FetchType.EAGER)
    @Setter(AccessLevel.PRIVATE)
    @JsonIgnore
    List<Ride> rideList;
    @Embedded
    private RoutePoint currentPosition;

}
