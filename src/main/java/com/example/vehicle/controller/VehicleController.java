package com.example.vehicle.controller;

import com.example.vehicle.constants.RespStatus;
import com.example.vehicle.model.VRNValidation;
import com.example.vehicle.model.VehicleInfoBody;
import com.example.vehicle.pojo.VehiclePojo;
import com.example.vehicle.service.VehicleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
public class VehicleController {
    @Autowired
    VehicleService vehicleService;


    @PostMapping("/addVehicle")
    ResponseEntity<VehicleInfoBody> addVehicle(@RequestBody @Valid VehiclePojo vehiclePojo) {
        if (vehiclePojo.getVehicleNumber()!=null && !vehiclePojo.getVehicleNumber().isEmpty()) {
            if (VRNValidation.isValid(vehiclePojo.getVehicleNumber())) {
                vehicleService.addVehicle(vehiclePojo);
                return new ResponseEntity<>(new VehicleInfoBody(RespStatus.SUCCESS), HttpStatus.OK);
            } else {
                return new ResponseEntity<>(new VehicleInfoBody(RespStatus.INVALID_VEHICLE_NUMBER + vehiclePojo.getVehicleNumber()), HttpStatus.BAD_REQUEST);
            }
        }
        return new ResponseEntity<>(new VehicleInfoBody(RespStatus.ENTER_VEHICLE_NUMBER), HttpStatus.BAD_REQUEST);
    }

    @PutMapping("/updateVehicle")
    ResponseEntity<VehicleInfoBody> updateVehicle(@RequestBody VehiclePojo vehiclePojo) {
        if (vehiclePojo.getVehicleNumber()!=null && !vehiclePojo.getVehicleNumber().isEmpty()) {
            if (VRNValidation.isValid(vehiclePojo.getVehicleNumber())) {
                vehicleService.updateVehicle(vehiclePojo);
                return new ResponseEntity<>(new VehicleInfoBody(RespStatus.UPDATED), HttpStatus.OK);
            } else {
                return new ResponseEntity<>(new VehicleInfoBody(RespStatus.INVALID_VEHICLE_NUMBER + vehiclePojo.getVehicleNumber()), HttpStatus.BAD_REQUEST);
            }
        }
        return new ResponseEntity<>(new VehicleInfoBody(RespStatus.ENTER_VEHICLE_NUMBER), HttpStatus.BAD_REQUEST);
    }

    @GetMapping("/getVehicle")
    ResponseEntity<VehiclePojo> getVehicle(@RequestParam("vehicleNumber")@Valid String vehicleNumber) {
        return new ResponseEntity<>(vehicleService.getVehicle(vehicleNumber),HttpStatus.OK);
    }

    @DeleteMapping("/deleteVehicle")
    String deleteBooking(@RequestParam("vehicleNumber")@Valid String vehicleNumber) {
        return vehicleService.deleteVehicle(vehicleNumber);
    }

}
