package com.example.vehicle.service;

import com.example.vehicle.pojo.VehiclePojo;
import com.example.vehicle.entity.VehicleEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface VehicleService {
    VehicleEntity addVehicle(VehiclePojo vehiclePojo, MultipartFile image) throws IOException;
    VehiclePojo getVehicle(String vehicleNumber);
    VehicleEntity updateVehicle(VehiclePojo vehiclePojo, MultipartFile image) throws IOException;
    String deleteVehicle(String vehicleNumber);

}
