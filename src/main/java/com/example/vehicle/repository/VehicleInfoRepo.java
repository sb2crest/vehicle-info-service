package com.example.vehicle.repository;

import com.example.vehicle.entity.VehicleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface VehicleInfoRepo extends JpaRepository<VehicleEntity,Integer> {
    @Query("select v from VehicleInfo v where v.vehicleNumber = :vehicleNumber")
    VehicleEntity getByVehicleNumber(String vehicleNumber);
}
