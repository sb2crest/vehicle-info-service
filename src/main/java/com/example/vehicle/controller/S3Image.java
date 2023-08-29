package com.example.vehicle.controller;

import com.example.vehicle.pojo.VehiclePojo;
import com.example.vehicle.service.S3ImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;


@RestController
public class S3Image {
    @Autowired
    private S3ImageService s3ImageService;

    @PostMapping("/uploadImage")
    public ResponseEntity<String> uploadImage(@ModelAttribute VehiclePojo vehiclePojo) throws IOException{
        return s3ImageService.uploadImage(vehiclePojo);
    }

  /*  @PostMapping("/uploadImage")
    public ResponseEntity<String> uploadVehicleImage(@RequestBody VehiclePojo vehiclePojo) throws IOException {
       return new ResponseEntity<>( s3ImageService.uploadVehicleImage(vehiclePojo),HttpStatus.OK);
    }*/
}
