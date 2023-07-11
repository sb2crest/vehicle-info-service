package com.example.vehicle.service;

import com.example.vehicle.pojo.VehiclePojo;
import com.example.vehicle.entity.VehicleEntity;

public interface VehicleService {
    VehicleEntity addVehicle(VehiclePojo vehiclePojo);
    VehiclePojo getVehicle(String vehicleNumber);
    VehicleEntity updateVehicle(VehiclePojo vehiclePojo);
    String deleteVehicle(String vehicleNumber);

}
