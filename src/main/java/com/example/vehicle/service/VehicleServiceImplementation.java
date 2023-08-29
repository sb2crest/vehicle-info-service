package com.example.vehicle.service;

import com.example.vehicle.exception.ResStatus;
import com.example.vehicle.exception.VehicleNumberException;
import com.example.vehicle.pojo.VehiclePojo;
import com.example.vehicle.entity.VehicleEntity;
import com.example.vehicle.repository.VehicleInfoRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class VehicleServiceImplementation implements VehicleService {
    @Autowired
    VehicleInfoRepo vehicleInfoRepo;

    @Override
    public VehicleEntity addVehicle(VehiclePojo vehiclePojo) {
            VehicleEntity vehicleEntity;
            vehicleEntity = vehicleInfoRepo.getByVehicleNumber(vehiclePojo.getVehicleNumber());
            if(vehicleEntity==null){
                vehicleEntity=new VehicleEntity();
                vehicleEntity.setSeatCapacity(vehiclePojo.getSeatCapacity());
                vehicleEntity.setVehicleNumber(vehiclePojo.getVehicleNumber());
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
        public VehicleEntity updateVehicle (VehiclePojo vehiclePojo){
            VehicleEntity vehicleEntity = vehicleInfoRepo.getByVehicleNumber(vehiclePojo.getVehicleNumber());
            if (vehicleEntity != null) {
                vehicleEntity.setSeatCapacity(vehiclePojo.getSeatCapacity());
                vehicleInfoRepo.save(vehicleEntity);
                return vehicleEntity;
            }
            return addVehicle(vehiclePojo);

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
