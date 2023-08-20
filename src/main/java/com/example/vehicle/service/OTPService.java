package com.example.vehicle.service;

import com.example.vehicle.pojo.ValidateOTP;

public interface OTPService {
    String validateSMS(ValidateOTP validateOTP);

    String generateOTP(String mobileNumber);
}
