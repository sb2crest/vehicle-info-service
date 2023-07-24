package com.example.vehicle.service;

import com.example.vehicle.entity.OTP;
import com.example.vehicle.pojo.ValidateOTP;
import com.example.vehicle.repository.OTPRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
class OTPServiceImplementationTest {
    @InjectMocks
    OTPServiceImplementation otpServiceImplementation;
    @Mock
    OTPRepository otpRepository;

    @Test
    void validateSMS() {
        Mockito.when(otpRepository.findByPhoneNumber(Mockito.anyString(),Mockito.any())).thenReturn(getOTPs());
        Assertions.assertEquals("Successfully validated", otpServiceImplementation.validateSMS(getValidateOTP()));
    }

    @Test
    void validateSMSForUnSuccessValidation() {
        Mockito.when(otpRepository.findByPhoneNumber(Mockito.anyString(),Mockito.any())).thenReturn(List.of());
        Assertions.assertEquals("Validation Unsuccessful", otpServiceImplementation.validateSMS(getValidateOTP()));
    }

    @Test
    void validateSMSForUnSuccessValidationWhenNoMatchingRecordFound() {
        List<OTP> list=getOTPs();
        list.get(0).setOtp("16544");
        Mockito.when(otpRepository.findByPhoneNumber(Mockito.anyString(),Mockito.any())).thenReturn(list);
        Assertions.assertEquals("Validation Unsuccessful", otpServiceImplementation.validateSMS(getValidateOTP()));
    }

    List<OTP> getOTPs(){
        List<OTP> otps=new ArrayList<>();

        OTP otp=new OTP();
        otp.setOtp("12345");
        otp.setMobileNumber("1234567890");
        otp.setId(1L);
        otp.setGeneratedTime(LocalDateTime.now());
        otps.add(otp);

        OTP otp1=new OTP();
        otp1.setOtp("13345");
        otp1.setMobileNumber("123448554644");
        otp1.setId(1L);
        otp1.setGeneratedTime(LocalDateTime.now());
        otps.add(otp1);
        return otps;
    }
    ValidateOTP getValidateOTP() {
        ValidateOTP validateOTP = new ValidateOTP();
        validateOTP.setOtp("12345");
        validateOTP.setMobileNumber("1234567890");
        return validateOTP;
    }
}