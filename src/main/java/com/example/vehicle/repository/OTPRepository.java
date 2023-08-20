package com.example.vehicle.repository;

import com.example.vehicle.entity.OTP;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OTPRepository extends JpaRepository<OTP,Long> {
    @Query("select o from OTP o where o.mobileNumber = :mobileNumber and o.generatedTime >= :localDateTime  Order By o.generatedTime desc")
    List<OTP> findByPhoneNumber(String mobileNumber, String localDateTime);
}
