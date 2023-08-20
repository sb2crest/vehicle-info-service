package com.example.vehicle.controller;

import com.example.vehicle.exception.ResStatus;
import com.example.vehicle.exception.VehicleNumberException;
import com.example.vehicle.pojo.ValidateOTP;
import com.example.vehicle.service.OTPService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@Slf4j
public class OTPController {
    private final OTPService otpService;

    @Autowired
    public OTPController(OTPService otpService) {
        this.otpService = otpService;
    }

    @GetMapping("/sendOTP")
    String getOTP(@RequestParam("mobileNumber") String mobileNumber) {
        checkMobileNumber(mobileNumber);
        return otpService.generateOTP(mobileNumber);
    }

    private void checkMobileNumber(String mobileNumber) {
        if(mobileNumber.isEmpty()){
            throw new VehicleNumberException(ResStatus.ENTER_NUMBER);
        }
        if (mobileNumber.length()!=10){
            throw new VehicleNumberException(ResStatus.MOBILE_DIGIT);
        }
    }

    @PostMapping("/validateOTP")
    String validateSMS(@RequestBody @Valid ValidateOTP validateOTP) {
        return otpService.validateSMS(validateOTP);
    }
}


