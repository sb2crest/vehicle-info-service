package com.example.vehicle.controller;

import com.example.vehicle.entity.VehicleEntity;
import com.example.vehicle.exception.ResStatus;
import com.example.vehicle.exception.VehicleNumberException;
import com.example.vehicle.model.VRNValidation;
import com.example.vehicle.pojo.VehiclePojo;
import com.example.vehicle.service.VehicleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@Slf4j
public class VehicleController {
    @Autowired
    VehicleService vehicleService;


    @PostMapping("/addVehicle")
    ResponseEntity<VehicleEntity> addVehicle(@RequestBody @Valid VehiclePojo vehiclePojo) {
        try {
            checkNumber(vehiclePojo);
            return new ResponseEntity<>(vehicleService.addVehicle(vehiclePojo), HttpStatus.OK);
        } catch (Exception e) {
            log.info("exception");
            throw e;
        }
    }

    private void checkNumber(VehiclePojo vehiclePojo) {
        if (vehiclePojo.getVehicleNumber() == null) {
            throw new VehicleNumberException(ResStatus.VEHICLE_NUMBER);
        }
        if (!VRNValidation.isValid(vehiclePojo.getVehicleNumber())) {
            throw new VehicleNumberException(ResStatus.INVALID_NUMBER);
        }
    }

    @PutMapping("/updateVehicle")
    ResponseEntity<VehicleEntity> updateVehicle(@RequestBody VehiclePojo vehiclePojo) {
        try {
            checkNumber(vehiclePojo);
            return new ResponseEntity<>(vehicleService.updateVehicle(vehiclePojo), HttpStatus.OK);
        } catch (Exception e) {
            log.info("exception");
            throw e;
        }
    }

    @GetMapping("/getVehicle")
    ResponseEntity<VehiclePojo> getVehicle(@RequestParam("vehicleNumber") @Valid String vehicleNumber) {
        return new ResponseEntity<>(vehicleService.getVehicle(vehicleNumber), HttpStatus.OK);
    }

    @DeleteMapping("/deleteVehicle")
    String deleteBooking(@RequestParam("vehicleNumber") @Valid String vehicleNumber) {
        return vehicleService.deleteVehicle(vehicleNumber);
    }

}
