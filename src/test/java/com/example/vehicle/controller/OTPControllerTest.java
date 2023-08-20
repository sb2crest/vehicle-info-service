package com.example.vehicle.controller;

import com.example.vehicle.model.TestUtil;
import com.example.vehicle.pojo.ValidateOTP;
import com.example.vehicle.service.OTPService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.io.IOException;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ExtendWith(SpringExtension.class)
@WebMvcTest(OTPController.class)
class OTPControllerTest {
    @MockBean
    OTPService otpService;
    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private MockMvc mvc;

    @BeforeEach
    public void setup() {
        mvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .build();
    }

    @Test
    void validateSMS() throws Exception {
        Mockito.when(otpService.validateSMS(Mockito.any())).thenReturn("Successfully validated");
        mvc.perform(post("/validateOTP").content(TestUtil.convertObjectToJsonBytes(getValidateOTP()))
                .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk());
    }

    @Test
    void validateSMSWithUnSuccess() throws Exception {
        Mockito.when(otpService.validateSMS(Mockito.any())).thenReturn("Validation Unsuccessful");
        mvc.perform(post("/validateOTP").content(TestUtil.convertObjectToJsonBytes(getValidateOTP()))
                .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk());
    }

    @Test
     void testGetOTP_SUCCESS() throws Exception {
        String mobileNumber = "1234567890";
        String expectedResult = "OTP sent successfully.";

        Mockito.when(otpService.generateOTP(mobileNumber)).thenReturn(expectedResult);

        mvc.perform(get("/sendOTP")
                        .param("mobileNumber", mobileNumber)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(expectedResult));
    }

    @Test
     void testGetOTP_FAILURE() throws Exception {
        String mobileNumber = "1234567890";
        String expectedResult = "Failed to send OTP.";

        Mockito.when(otpService.generateOTP(mobileNumber)).thenReturn(expectedResult);

        mvc.perform(get("/sendOTP")
                        .param("mobileNumber", mobileNumber)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(expectedResult));
    }
    @Test
     void testGetOTP_InvalidNumber() throws Exception {
        String mobileNumber = "123"; // Invalid number

        mvc.perform(get("/sendOTP")
                        .param("mobileNumber", mobileNumber)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
    @Test
     void testGetOTP_EmptyMobileNumber() throws Exception {
        String mobileNumber = ""; // Invalid number

        mvc.perform(get("/sendOTP")
                        .param("mobileNumber", mobileNumber)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    ValidateOTP getValidateOTP() {
        ValidateOTP validateOTP = new ValidateOTP();
        validateOTP.setOtp("12345");
        validateOTP.setMobileNumber("1234567890");
        return validateOTP;
    }
}