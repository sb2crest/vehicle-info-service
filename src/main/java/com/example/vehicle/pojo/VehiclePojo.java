package com.example.vehicle.pojo;


import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Setter
@Getter
public class VehiclePojo {

    private Integer seatCapacity;

    private String vehicleNumber;

    private MultipartFile image;

}
