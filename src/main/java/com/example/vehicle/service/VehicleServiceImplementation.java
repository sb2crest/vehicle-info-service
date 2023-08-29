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
import java.util.UUID;

@Service
public class VehicleServiceImplementation implements VehicleService {
    @Autowired
    VehicleInfoRepo vehicleInfoRepo;

    @Autowired
    private AmazonS3 amazonS3;

    @Value("${aws.bucketName}")
    private String s3BucketName;

    @Override
    public VehicleEntity addVehicle(VehiclePojo vehiclePojo, MultipartFile image) throws IOException {
            VehicleEntity vehicleEntity;
            vehicleEntity = vehicleInfoRepo.getByVehicleNumber(vehiclePojo.getVehicleNumber());
            if(vehicleEntity==null){
                vehicleEntity=new VehicleEntity();
                vehicleEntity.setSeatCapacity(vehiclePojo.getSeatCapacity());
                vehicleEntity.setVehicleNumber(vehiclePojo.getVehicleNumber());

                String fileName = UUID.randomUUID().toString() + "-" + image.getOriginalFilename();
                InputStream inputStream = image.getInputStream();
                ObjectMetadata metadata = new ObjectMetadata();
                metadata.setContentLength(image.getSize());
                amazonS3.putObject(s3BucketName, fileName, inputStream, metadata);
                String s3ImageUrl = amazonS3.getUrl(s3BucketName, fileName).toString();
                vehicleEntity.setS3ImageUrl(s3ImageUrl);
                vehicleInfoRepo.save(vehicleEntity);

                return vehicleEntity;

            }else{
                throw new VehicleNumberException(ResStatus.DUPLICATE_NUMBER);
            }
        }

        @Override
        public VehiclePojo getVehicle (String vehicleNumber){
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
        public VehicleEntity updateVehicle (VehiclePojo vehiclePojo, MultipartFile image) throws IOException {
            VehicleEntity vehicleEntity = vehicleInfoRepo.getByVehicleNumber(vehiclePojo.getVehicleNumber());
            if (vehicleEntity != null) {
                vehicleEntity.setSeatCapacity(vehiclePojo.getSeatCapacity());
                vehicleInfoRepo.save(vehicleEntity);
                return vehicleEntity;
            }
            return addVehicle(vehiclePojo,image);

        }

        @Override
        public String deleteVehicle (String vehicleNumber){
            VehicleEntity vehicleEntity = vehicleInfoRepo.getByVehicleNumber(vehicleNumber);
            if (vehicleEntity != null) {
                vehicleInfoRepo.delete(vehicleEntity);
                return "deleted Successfully";
            }
            return "no vehicle with this number " + vehicleNumber;
        }

    }
