package com.example.vehicle.service;

import com.amazonaws.services.s3.AmazonS3;
import com.example.vehicle.exception.VehicleNumberException;
import com.example.vehicle.pojo.VehiclePojo;
import com.example.vehicle.entity.VehicleEntity;
import com.example.vehicle.repository.VehicleInfoRepo;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URL;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
class VehicleServiceImplementationTest {

    @Mock
    VehicleInfoRepo vehicleInfoRepo;

    @BeforeEach
    public void setUp() {
        Mockito.reset(vehicleInfoRepo);
        ReflectionTestUtils.setField(bookingServiceImplementation,"s3BucketName","abc");
    }
    @InjectMocks
    VehicleServiceImplementation bookingServiceImplementation;

    @Mock
    private AmazonS3 amazonS3;

    @Test
    void testAddVehicle_Success() throws IOException {
        VehiclePojo vehiclePojo = getVehiclePojo();
        MultipartFile image = new MockMultipartFile("testImage.jpg", "testImage.jpg", "image/jpeg", "test".getBytes());
        when(vehicleInfoRepo.getByVehicleNumber(any())).thenReturn(null);
        String s3ImageUrl = "https://s3.example.com/testImage.jpg";
        when(amazonS3.getUrl(any(), any())).thenReturn(new URL(s3ImageUrl));
        VehicleEntity result = bookingServiceImplementation.addVehicle(vehiclePojo, image);
        assertEquals(vehiclePojo.getVehicleNumber(), result.getVehicleNumber());
        assertEquals(vehiclePojo.getSeatCapacity(), result.getSeatCapacity());
        assertEquals(s3ImageUrl, result.getS3ImageUrl());
        verify(vehicleInfoRepo, times(1)).getByVehicleNumber(any());
    }

    @Test()
    void testAddVehicle_DuplicateNumber() throws IOException {
        VehiclePojo vehiclePojo = getVehiclePojo();
        MultipartFile image = new MockMultipartFile("testImage.jpg", "testImage.jpg", "image/jpeg", "test".getBytes());
        when(vehicleInfoRepo.getByVehicleNumber(any())).thenReturn(new VehicleEntity());
        assertThrows(VehicleNumberException.class, () ->bookingServiceImplementation.addVehicle(vehiclePojo, image));
    }

    @Test
    void getVehicle(){
        when(vehicleInfoRepo.getByVehicleNumber(Mockito.anyString())).thenReturn(getVehicleEntity());
        assertEquals(4,bookingServiceImplementation.getVehicle("12").getSeatCapacity());
    }
    @Test
    void getVehicleWHenNoMatchingRecord(){
        when(vehicleInfoRepo.getByVehicleNumber(Mockito.anyString())).thenReturn(null);
        Assertions.assertNull(bookingServiceImplementation.getVehicle("12"));
    }

    @Test
    void testUpdateVehicle_IfDataExist() throws IOException {
        VehiclePojo vehiclePojo = getVehiclePojo();
        MultipartFile image = new MockMultipartFile("image.jpg", new byte[0]);
        VehicleEntity existingEntity = getVehicleEntity();
        existingEntity.setS3ImageUrl("s3://bucket/old-image.jpg");
        when(vehicleInfoRepo.getByVehicleNumber("ABC123")).thenReturn(existingEntity);
        doNothing().when(amazonS3).deleteObject(anyString(), anyString());
        when(amazonS3.getUrl(anyString(), anyString())).thenReturn(new URL("https://bucket.s3.amazonaws.com/image.jpg"));
        when(vehicleInfoRepo.save(any(VehicleEntity.class))).thenReturn(existingEntity);
        VehicleEntity result = bookingServiceImplementation.updateVehicle(vehiclePojo, image);
        assertNotNull(result);
        assertEquals("12", result.getVehicleNumber());
        assertEquals("https://bucket.s3.amazonaws.com/image.jpg", result.getS3ImageUrl());
    }

    @Test
    void testUpdateVehicle_WhichDoesNotExist() throws IOException {
        VehiclePojo vehiclePojo = getVehiclePojo();
        MultipartFile image = new MockMultipartFile("image.jpg", new byte[0]);
        VehicleEntity existingEntity = new VehicleEntity();
        existingEntity.setVehicleNumber("ABC123");
        existingEntity.setS3ImageUrl("s3://bucket/old-image.jpg");
        String s3ImageUrl = "https://s3.example.com/testImage.jpg";
        when(amazonS3.getUrl(any(), any())).thenReturn(new URL(s3ImageUrl));
        when(vehicleInfoRepo.getByVehicleNumber("ABC123")).thenReturn(null);
        VehicleEntity result = bookingServiceImplementation.updateVehicle(vehiclePojo, image);
        assertNotNull(result);
    }

    @Test
    void testExtractS3KeyFromUrl() {
        String s3Url = "https://bucket.s3.amazonaws.com/image.jpg";
        String result = bookingServiceImplementation.extractS3KeyFromUrl(s3Url);
        assertEquals("image.jpg", result);
    }

    @Test
    void deleteBooking() {
        when(vehicleInfoRepo.getByVehicleNumber(Mockito.anyString())).thenReturn(getVehicleEntity());
        assertEquals("deleted Successfully",bookingServiceImplementation.deleteVehicle("12"));
    }

    @Test
    void deleteBookingForNull() {
        when(vehicleInfoRepo.getByVehicleNumber(Mockito.anyString())).thenReturn(null);
        assertEquals("no vehicle with this number 12",bookingServiceImplementation.deleteVehicle("12"));
    }
    VehiclePojo getVehiclePojo(){
        VehiclePojo vehiclePojo =new VehiclePojo();
        vehiclePojo.setSeatCapacity(4);
        vehiclePojo.setVehicleNumber("12");
        return vehiclePojo;
    }
    VehicleEntity getVehicleEntity(){
        VehicleEntity vehicleEntity =new VehicleEntity();
        vehicleEntity.setSeatCapacity(4);
        vehicleEntity.setVehicleNumber("12");
        return vehicleEntity;
    }
}