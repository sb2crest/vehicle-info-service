package com.example.vehicle.service;

import com.example.vehicle.entity.OTP;
import com.example.vehicle.pojo.ValidateOTP;
import com.example.vehicle.repository.OTPRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class OTPServiceImplementation implements OTPService{

    @Autowired
    OTPRepository otpRepository;
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    @Value("${api.key}")
    private String apiKey;
    @Value("${sms.url}")
    private String smsUrl;
    @Override
    public String validateSMS(ValidateOTP validateOTP) {
        List<OTP> responseOTP=otpRepository.findByPhoneNumber(validateOTP.getMobileNumber(), formatter.format(LocalDateTime.now().minusMinutes(5)));
        if(!responseOTP.isEmpty() && responseOTP.stream().anyMatch(o->o.getOtPassword().equals(validateOTP.getOtp()))){
                 return "Successfully validated";
        }
        return "Validation Unsuccessful";
    }

    @Override
    public String generateOTP(String mobileNumber) {
        String otp = generateRandomOTP();
        String apiUrl = smsUrl+apiKey +
                "&variables_values=" + otp +
                "&route=otp&numbers=" + mobileNumber;
        try {
            boolean success = sendOtp(apiUrl);
            if (success) {
                saveOTPToDB(mobileNumber,otp);
                return "OTP sent successfully.";
            } else {
                return "Failed to send OTP.";
            }
        } catch (IOException e) {
            return "Failed to send OTP: " + e.getMessage();
        }
    }

    private boolean sendOtp(String apiUrl) throws IOException {
        URL url = new URL(apiUrl);
        HttpURLConnection connection = createConnection(url);
        connection.setRequestMethod("GET");

        int responseCode = connection.getResponseCode();
        if (responseCode != 200) {
            return false;
        }

        try (BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
            StringBuilder response = new StringBuilder();
            String responseLine;
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }
            return true;
        }
    }

    protected  HttpURLConnection createConnection(URL url) throws IOException {
        return (HttpURLConnection) url.openConnection();
    }
    private  String generateRandomOTP() {
        int otp = (int) (Math.random() * 900000) + 100000;
        return String.valueOf(otp);
    }

    private void saveOTPToDB(String mobileNumber, String generatedOTP) {
        OTP otp=new OTP();
        otp.setMobileNumber(mobileNumber);
        otp.setOtPassword(generatedOTP);
        otp.setGeneratedTime(formatter.format(LocalDateTime.now()));
        otpRepository.save(otp);
    }

}
