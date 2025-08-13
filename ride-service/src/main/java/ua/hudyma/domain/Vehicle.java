package ua.hudyma.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;
import ua.hudyma.enums.VehicleClass;
import ua.hudyma.enums.VehicleColor;
import ua.hudyma.enums.VehicleModel;
import ua.hudyma.enums.VehiclePropulsion;

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

    @OneToMany(mappedBy = "vehicle")
    @Setter(AccessLevel.PRIVATE)
    @JsonIgnore
    List<Ride> rideList;

}
