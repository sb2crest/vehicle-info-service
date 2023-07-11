package com.example.vehicle.pojo;

import lombok.Data;

@Data
public class VehiclePojo {

    private String name;

    private String model;

    private Integer seatCapacity;

    //@Pattern(regexp = "(?=[A-Z]{2})(?=\\s)(?=[A-Z\\d]+)(?=\\s)(?=\\d{4}){8,10}$",message = "enter valid vehicle number")
    private String vehicleNumber;
}
