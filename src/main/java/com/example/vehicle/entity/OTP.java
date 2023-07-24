package com.example.vehicle.entity;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "otp")
public class OTP {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "mobile")
    private String mobileNumber;

    private String otp;

    @Column(name = "generated_time")
    private LocalDateTime generatedTime;

}
