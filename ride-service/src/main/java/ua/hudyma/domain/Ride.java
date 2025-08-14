package ua.hudyma.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import ua.hudyma.dto.RoutePoint;
import ua.hudyma.enums.RideStatus;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "rides")
@Data
public class Ride {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @JsonFormat(pattern = "dd-MM-yyyy HH:mm")
    @CreationTimestamp
    private Date requestedOn;
    @Column(nullable = false)
    private String driverId;
    @Column(nullable = false)
    private String paxId;
    @ManyToOne(optional = false)
    @JoinColumn(name = "vehicle_id")
    private Vehicle vehicle;
    private String paymentId;
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "latitude",
                    column = @Column(name = "departure_latitude", nullable = false)),
            @AttributeOverride(name = "longitude",
                    column = @Column(name = "departure_longitude", nullable = false))
    })
    private RoutePoint departure;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "latitude",
                    column = @Column(name = "destination_latitude", nullable = false)),
            @AttributeOverride(name = "longitude",
                    column = @Column(name = "destination_longitude", nullable = false))
    })
    private RoutePoint destination;

    @Column(nullable = false)
    @Positive
    @NotNull
    BigDecimal ridePrice;

    @Enumerated(value = EnumType.STRING)
    @Column(nullable = false)
    private RideStatus rideStatus;

    @Transient
    private List<double[]> routeList;
    //todo fetch track from GraphHopper


}
