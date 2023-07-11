package com.example.vehicle.entity;

import lombok.*;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "vehicle_info")
@Getter
@Setter
@ToString
@RequiredArgsConstructor
public class VehicleEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long vId;

    @Column(name="name")
    private String name;

    @Column(name="model")
    private String model;

    @Column(name="seat_capacity")
    private Integer seatCapacity;

    @Column(name="vehicle_number")
    private String vehicleNumber;

//    @ManyToOne
//    @JoinColumn(name = "v_id",updatable = false,insertable = false)
//    private Integer vehicleId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        VehicleEntity that = (VehicleEntity) o;
        return vId != null && Objects.equals(vId, that.vId);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
