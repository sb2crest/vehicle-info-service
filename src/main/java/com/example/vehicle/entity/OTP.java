package com.example.vehicle.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Setter
@Getter
@Entity
@Table(name = "otp")
public class OTP {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "mobile")
    private String mobileNumber;

    private String otPassword;

    @Column(name = "generated_time")
    private String generatedTime;

}
