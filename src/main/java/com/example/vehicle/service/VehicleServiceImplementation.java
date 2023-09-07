package com.example.vehicle.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.example.vehicle.exception.ResStatus;
import com.example.vehicle.exception.VehicleNumberException;
import com.example.vehicle.pojo.VehiclePojo;
import com.example.vehicle.entity.VehicleEntity;
import com.example.vehicle.repository.VehicleInfoRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Service
public class VehicleServiceImplementation implements VehicleService {
    @Autowired
    VehicleInfoRepo vehicleInfoRepo;
    @Autowired
    AmazonS3 amazonS3;

    @Value("${aws.bucketName}")
    String s3BucketName;


    @Override
    public VehicleEntity addVehicle(VehiclePojo vehiclePojo, MultipartFile image) throws IOException {
        VehicleEntity vehicleEntity;
        vehicleEntity = vehicleInfoRepo.getByVehicleNumber(vehiclePojo.getVehicleNumber());
        if (vehicleEntity == null) {
            vehicleEntity = new VehicleEntity();
            vehicleEntity.setSeatCapacity(vehiclePojo.getSeatCapacity());
            vehicleEntity.setVehicleNumber(vehiclePojo.getVehicleNumber());
            vehicleEntity.setIsVehicleAC(vehiclePojo.getIsVehicleAC());
            vehicleEntity.setIsVehicleSleeper(vehiclePojo.getIsVehicleSleeper());

            String s3ImageUrl = uploadImageToS3Bucket(image, vehiclePojo);
            vehicleEntity.setS3ImageUrl(s3ImageUrl);
            vehicleInfoRepo.save(vehicleEntity);
            return vehicleEntity;
        } else {
            throw new VehicleNumberException(ResStatus.DUPLICATE_NUMBER);
        }
    }

    @Override
    public VehiclePojo getVehicle(String vehicleNumber) {
        VehicleEntity vehicleEntity = vehicleInfoRepo.getByVehicleNumber(vehicleNumber);
        VehiclePojo vehiclePojo = null;
        if (vehicleEntity != null) {
            vehiclePojo = new VehiclePojo();
            vehiclePojo.setSeatCapacity(vehicleEntity.getSeatCapacity());
            vehiclePojo.setVehicleNumber(vehicleEntity.getVehicleNumber());
        }
        return vehiclePojo;
    }

    @Override
    public VehicleEntity updateVehicle(VehiclePojo vehiclePojo, MultipartFile image) throws IOException {
        VehicleEntity vehicleEntity = vehicleInfoRepo.getByVehicleNumber(vehiclePojo.getVehicleNumber());
        if (vehicleEntity == null) {
            return addVehicle(vehiclePojo, image);
        }
        // Delete old images from S3
        String oldImageUrl = vehicleEntity.getS3ImageUrl();
        if (oldImageUrl != null && !image.isEmpty()) {
            String oldKey = extractS3KeyFromUrl(oldImageUrl);
            amazonS3.deleteObject(s3BucketName, oldKey);
        }
        // Upload new image to S3
        String s3ImageUrl = uploadImageToS3Bucket(image, vehiclePojo);
        vehicleEntity.setS3ImageUrl(s3ImageUrl);
        vehicleEntity.setSeatCapacity(vehiclePojo.getSeatCapacity());
        vehicleEntity.setVehicleNumber(vehiclePojo.getVehicleNumber());
        vehicleEntity.setIsVehicleAC(vehiclePojo.getIsVehicleAC());
        vehicleEntity.setIsVehicleSleeper(vehiclePojo.getIsVehicleSleeper());
        return vehicleInfoRepo.save(vehicleEntity);
    }

    String extractS3KeyFromUrl(String s3Url) {
        String[] parts = s3Url.split("/");
        return parts[parts.length - 1];
    }

    String uploadImageToS3Bucket(MultipartFile image, VehiclePojo vehiclePojo) throws IOException {
        String fileName = vehiclePojo.getVehicleNumber();
        InputStream inputStream = image.getInputStream();
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType("jpeg");
        metadata.setContentLength(image.getSize());
        amazonS3.putObject(s3BucketName, fileName, inputStream, metadata);
        return amazonS3.getUrl(s3BucketName, fileName).toString();
    }

    @Override
    public String deleteVehicle(String vehicleNumber) {
        VehicleEntity vehicleEntity = vehicleInfoRepo.getByVehicleNumber(vehicleNumber);
        if (vehicleEntity != null) {
            vehicleInfoRepo.delete(vehicleEntity);
            return "deleted Successfully";
        }
        return "no vehicle with this number " + vehicleNumber;
    }

    @Override
    public List<VehiclePojo> listAllVehicles() {
        List<VehiclePojo> vehiclePojoList = new ArrayList<>();

        try {
            List<VehicleEntity> vehicleEntityList = vehicleInfoRepo.findAll();

            for (VehicleEntity vehicleEntity : vehicleEntityList) {
                VehiclePojo vehiclePojo = new VehiclePojo();
                vehiclePojo.setVehicleNumber(vehicleEntity.getVehicleNumber());
                vehiclePojo.setSeatCapacity(vehicleEntity.getSeatCapacity());
                vehiclePojo.setImageUrl(vehicleEntity.getS3ImageUrl());
                vehiclePojo.setIsVehicleAC(vehicleEntity.getIsVehicleAC());
                vehiclePojo.setIsVehicleSleeper(vehicleEntity.getIsVehicleSleeper());

                vehiclePojoList.add(vehiclePojo);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return vehiclePojoList;
    }
}
