package com.example.vehicle.service;

import com.example.vehicle.entity.OTP;
import com.example.vehicle.pojo.ValidateOTP;
import com.example.vehicle.repository.OTPRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;


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
    void validateSMSForUnSuccessfulValidation() {
        Mockito.when(otpRepository.findByPhoneNumber(Mockito.anyString(),Mockito.any())).thenReturn(List.of());
        Assertions.assertEquals("Validation Unsuccessful", otpServiceImplementation.validateSMS(getValidateOTP()));
    }

    @Test
    void validateSMSForUnSuccessfulValidationWhenNoMatchingRecordFound() {
        List<OTP> list=getOTPs();
        list.get(0).setOtPassword("16544");
        Mockito.when(otpRepository.findByPhoneNumber(Mockito.anyString(),Mockito.any())).thenReturn(list);
        Assertions.assertEquals("Validation Unsuccessful", otpServiceImplementation.validateSMS(getValidateOTP()));
    }

    @Test
     void testGenerateOTP_SuccessfulSend() throws IOException {
        ReflectionTestUtils.setField(otpServiceImplementation,"apiKey","Nu9NlFqe7Q3kRQRv1a168kfYqu6aDx9y6Wxy8kUpOyddGolHsw9xtEtd3xWw");
        ReflectionTestUtils.setField(otpServiceImplementation,"smsUrl","https://www.fast2sms.com/dev/bulkV2?authorization=");
        String mobileNumber = "9535858675";

        OTPServiceImplementation spyService = spy(otpServiceImplementation);

        HttpURLConnection mockConnection = mock(HttpURLConnection.class);
        when(mockConnection.getResponseCode()).thenReturn(200);
        String mockResponse = "Mock response";
        ByteArrayInputStream inputStream = new ByteArrayInputStream(mockResponse.getBytes(StandardCharsets.UTF_8));
        when(mockConnection.getInputStream()).thenReturn(inputStream);
        doReturn(mockConnection).when(spyService).createConnection(any());

        String result = spyService.generateOTP(mobileNumber);

        verify(otpRepository, times(1)).save(any(OTP.class));
        assertEquals("OTP sent successfully.", result);
    }
    @Test
     void testGenerateOTP_FailedSend() throws IOException {
        ReflectionTestUtils.setField(otpServiceImplementation,"apiKey","abc");
        ReflectionTestUtils.setField(otpServiceImplementation,"smsUrl","https://www.fast2sms.com/dev/bulkV2?authorization=");
        String mobileNumber = "9535858675";

        HttpURLConnection mockConnection = mock(HttpURLConnection.class);
        when(mockConnection.getResponseCode()).thenReturn(401);

        String result = otpServiceImplementation.generateOTP(mobileNumber);

        verifyNoInteractions(otpRepository);
        assertEquals("Failed to send OTP.", result);
    }
    @Test
     void testGenerateOTP_whenException_throwException() throws IOException {
           HttpURLConnection mockConnection = mock(HttpURLConnection.class);
           when(mockConnection.getResponseCode()).thenReturn(401);
        String response = otpServiceImplementation.generateOTP(null);

    }


    List<OTP> getOTPs(){
        List<OTP> otps=new ArrayList<>();

        OTP otp=new OTP();
        otp.setOtPassword("12345");
        otp.setMobileNumber("1234567890");
        otp.setId(1L);
        otp.setGeneratedTime("2023-02-11");
        otps.add(otp);

        OTP otp1=new OTP();
        otp1.setOtPassword("13345");
        otp1.setMobileNumber("123448554644");
        otp1.setId(1L);
        otp1.setGeneratedTime("2023-02-11");
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