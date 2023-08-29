package com.example.vehicle.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.example.vehicle.entity.VehicleEntity;
import com.example.vehicle.pojo.VehiclePojo;
import com.example.vehicle.repository.ImageRepository;
import com.example.vehicle.repository.VehicleInfoRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
@Slf4j
public class
S3ImageService {

    @Value("${aws.bucketName}")
    private String bucketName;
    @Autowired
    private AmazonS3 amazonS3Client;
    @Autowired
    private VehicleInfoRepo vehicleRepository;

    @Autowired
    private ImageRepository imageRepository;

    public ResponseEntity<String> uploadImage(VehiclePojo vehiclePojo) {
        try {
            // Save image to S3 bucket
            String s3ImageUrl = saveImageToS3(vehiclePojo.getImage());

            // Save S3 URL to the database
            VehicleEntity imageEntity = new VehicleEntity();
            imageEntity.setImageUrl(s3ImageUrl);
            imageRepository.save(imageEntity);

            return ResponseEntity.ok("Image uploaded successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Image upload failed");
        }
    }

    private String saveImageToS3(MultipartFile image) throws IOException {
        String bucketName = "your-s3-bucket-name";
        String fileName = UUID.randomUUID().toString() + "-" + image.getOriginalFilename();

        amazonS3Client.putObject(new PutObjectRequest(bucketName, fileName, image.getInputStream(), null)
                .withCannedAcl(CannedAccessControlList.PublicRead));

        return amazonS3Client.getUrl(bucketName, fileName).toString();
    }


   /* public String uploadVehicleImage(VehiclePojo request) throws IOException {
        MultipartFile file = convertImagePathToMultipartFile(String.valueOf(request.getImage()));
        String imageUrl = uploadImage(file, request.getVehicleNumber());

        VehicleEntity vehicleEntity = new VehicleEntity();
        vehicleEntity.setVehicleNumber(request.getVehicleNumber());
        vehicleEntity.setImageUrl(imageUrl);

        vehicleRepository.save(vehicleEntity);
        return imageUrl;
    }

    public String getImageUrlByVehicleNumber(String vehicleNumber) {
        VehicleEntity vehicleEntity = vehicleRepository.getByVehicleNumber(vehicleNumber);
        if (vehicleEntity != null) {
            return vehicleEntity.getImageUrl();
        }
        return null;
    }

    private MultipartFile convertImagePathToMultipartFile(String imagePath) throws IOException {
        Path tempFile = Files.createTempFile("temp", ".tmp");
        Files.copy(new File(imagePath).toPath(), tempFile, StandardCopyOption.REPLACE_EXISTING);

         return new MockMultipartFile("file",
                new File(imagePath).getName(),
                Files.probeContentType(tempFile),
                Files.newInputStream(tempFile));
    }

    public String uploadImage(MultipartFile file, String filename) {
        try {
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType(file.getContentType());
            metadata.setContentLength(file.getSize());

            amazonS3Client.putObject(bucketName, filename, file.getInputStream(), metadata);

            return amazonS3Client.getUrl(bucketName, filename).toString();
        } catch (IOException e) {
            // Handle exceptions
            return null;
        }
    }*/
}
