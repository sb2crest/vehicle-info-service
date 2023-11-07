package com.example.vehicle.pojo;


import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Setter
@Getter
public class  VehiclePojo {

    private Integer seatCapacity;

    private String vehicleNumber;

    private MultipartFile image;

    private List<String> imageUrl;

    private Boolean isVehicleAC;

    private Boolean isVehicleSleeper;

}
