package com.example.vehicle.service;

import com.example.vehicle.entity.OTP;
import com.example.vehicle.pojo.ValidateOTP;
import com.example.vehicle.repository.OTPRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class OTPServiceImplementation implements OTPService{

    @Autowired
    OTPRepository otpRepository;
    @Override
    public String validateSMS(ValidateOTP validateOTP) {
        List<OTP> responseOTP=otpRepository.findByPhoneNumber(validateOTP.getMobileNumber(), LocalDateTime.now().minusMinutes(5));
        if(!responseOTP.isEmpty() && responseOTP.stream().anyMatch(o->o.getOtp().equals(validateOTP.getOtp()))){
                 return "Successfully validated";
        }
        return "Validation Unsuccessful";
    }
}
