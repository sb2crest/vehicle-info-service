package com.example.vehicle.service;

import com.example.vehicle.pojo.VehiclePojo;
import com.example.vehicle.entity.VehicleEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface VehicleService {
    VehicleEntity addVehicle(VehiclePojo vehiclePojo, List<MultipartFile> images) throws IOException;
    VehiclePojo getVehicle(String vehicleNumber);
    VehicleEntity updateVehicle(VehiclePojo vehiclePojo, List<MultipartFile> images) throws IOException;
    String deleteVehicle(String vehicleNumber);

    List<VehiclePojo> listAllVehicles();
}
