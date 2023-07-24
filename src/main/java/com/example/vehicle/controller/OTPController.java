package com.example.vehicle.controller;

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

    @PostMapping("/validateOTP")
    String validateSMS(@RequestBody @Valid ValidateOTP validateOTP) {
        return otpService.validateSMS(validateOTP);
    }
}


