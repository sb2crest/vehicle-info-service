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
    public VehicleEntity addVehicle(VehiclePojo vehiclePojo, List<MultipartFile> images) throws IOException {
        VehicleEntity vehicleEntity;
        vehicleEntity = vehicleInfoRepo.getByVehicleNumber(vehiclePojo.getVehicleNumber());
        if (vehicleEntity == null) {
            vehicleEntity = new VehicleEntity();
            vehicleEntity.setSeatCapacity(vehiclePojo.getSeatCapacity());
            vehicleEntity.setVehicleNumber(vehiclePojo.getVehicleNumber());
            vehicleEntity.setIsVehicleAC(vehiclePojo.getIsVehicleAC());
            vehicleEntity.setIsVehicleSleeper(vehiclePojo.getIsVehicleSleeper());
            if (images != null) {
                List<String> s3ImageUrl = uploadImagesToS3Bucket(images, vehiclePojo);
                vehicleEntity.setS3ImageUrl(s3ImageUrl);
            }
            vehicleEntity.setS3ImageUrl(vehicleEntity.getS3ImageUrl());
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
    public VehicleEntity updateVehicle(VehiclePojo vehiclePojo, List<MultipartFile> images) throws IOException {
        VehicleEntity vehicleEntity = vehicleInfoRepo.getByVehicleNumber(vehiclePojo.getVehicleNumber());
        if (vehicleEntity == null) {
            return addVehicle(vehiclePojo, images);
        }
        // Delete old images from S3
        List<String> oldImageUrl = vehicleEntity.getS3ImageUrl();
        if (oldImageUrl != null && !images.isEmpty()) {
            List<String> oldKeys = extractS3KeyFromUrl(oldImageUrl);
            for (String oldKey : oldKeys) {
                amazonS3.deleteObject(s3BucketName, oldKey);
            }
        }
        // Upload new images to S3
        List<String> s3ImageUrl = uploadImagesToS3Bucket(images,vehiclePojo);
        vehicleEntity.setS3ImageUrl(s3ImageUrl);
        vehicleEntity.setSeatCapacity(vehiclePojo.getSeatCapacity());
        vehicleEntity.setVehicleNumber(vehiclePojo.getVehicleNumber());
        vehicleEntity.setIsVehicleAC(vehiclePojo.getIsVehicleAC());
        vehicleEntity.setIsVehicleSleeper(vehiclePojo.getIsVehicleSleeper());
        return vehicleInfoRepo.save(vehicleEntity);
    }

    List<String> extractS3KeyFromUrl(List<String> s3ImageUrls) {
        List<String> updatedS3Keys = new ArrayList<>();
        for (String s3ImageUrl : s3ImageUrls) {
            String[] parts = s3ImageUrl.split("/");
            String oldKeyName = parts[parts.length - 1];
            updatedS3Keys.add(oldKeyName);
        }
        return updatedS3Keys;
    }

    List<String> uploadImagesToS3Bucket(List<MultipartFile> images, VehiclePojo vehiclePojo) {
        List<String> uploadedImageUrls = new ArrayList<>();
        for (MultipartFile file : images) {
            String fileName = generateFileName(file.getOriginalFilename(), vehiclePojo);
            try {
                InputStream inputStream = file.getInputStream();
                ObjectMetadata metadata = new ObjectMetadata();
                metadata.setContentType("jpeg");
                metadata.setContentLength(file.getSize());
                amazonS3.putObject(s3BucketName, fileName, inputStream, metadata);
                String fileUrl = amazonS3.getUrl(s3BucketName, fileName).toString();
                uploadedImageUrls.add(fileUrl);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return uploadedImageUrls;

    }


    int nameCounter = 1;
    private String generateFileName(String originalFileName, VehiclePojo vehiclePojo) {
            String imageName = vehiclePojo.getVehicleNumber() + "_image" + nameCounter;
            String extension = getFileExtension(originalFileName);
            nameCounter++;
            return imageName + extension;
    }
    private String getFileExtension(String fileName) {
        int lastDotIndex = fileName.lastIndexOf(".");
        if (lastDotIndex >= 0) {
            return fileName.substring(lastDotIndex);
        } else {
            return "";
        }
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
