package com.example.vehicle.service;

import com.amazonaws.services.s3.AmazonS3;
import com.example.vehicle.exception.VehicleNumberException;
import com.example.vehicle.pojo.VehiclePojo;
import com.example.vehicle.entity.VehicleEntity;
import com.example.vehicle.repository.VehicleInfoRepo;
import org.aspectj.lang.annotation.Before;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URL;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
class VehicleServiceImplementationTest {

    @Mock
    VehicleInfoRepo vehicleInfoRepo;

    @Before("")
    public void setUp() {
        Mockito.reset(vehicleInfoRepo);
    }
    @InjectMocks
    VehicleServiceImplementation bookingServiceImplementation;

    @Mock
    private AmazonS3 amazonS3;

    @Test
    public void testAddVehicle_Success() throws IOException {
        VehiclePojo vehiclePojo = new VehiclePojo();
        vehiclePojo.setVehicleNumber("ABC123");
        vehiclePojo.setSeatCapacity(4);
        MultipartFile image = new MockMultipartFile("testImage.jpg", "testImage.jpg", "image/jpeg", "test".getBytes());
        when(vehicleInfoRepo.getByVehicleNumber(any())).thenReturn(null);
        String s3ImageUrl = "https://s3.example.com/testImage.jpg";
        when(amazonS3.getUrl(any(), any())).thenReturn(new URL(s3ImageUrl));
        VehicleEntity result = bookingServiceImplementation.addVehicle(vehiclePojo, image);
        assertEquals(vehiclePojo.getVehicleNumber(), result.getVehicleNumber());
        assertEquals(vehiclePojo.getSeatCapacity(), result.getSeatCapacity());
        assertEquals(s3ImageUrl, result.getS3ImageUrl());
        verify(vehicleInfoRepo, times(1)).getByVehicleNumber(any());
        verify(vehicleInfoRepo, times(1)).save(any());
        verify(amazonS3, times(1)).putObject(any(), any(), any(), any());
    }

    @Test()
    void testAddVehicle_DuplicateNumber() throws IOException {
        VehiclePojo vehiclePojo = new VehiclePojo();
        vehiclePojo.setVehicleNumber("ABC123");
        vehiclePojo.setSeatCapacity(4);
        MultipartFile image = new MockMultipartFile("testImage.jpg", "testImage.jpg", "image/jpeg", "test".getBytes());
        when(vehicleInfoRepo.getByVehicleNumber(any())).thenReturn(new VehicleEntity());
        assertThrows(VehicleNumberException.class, () ->bookingServiceImplementation.addVehicle(vehiclePojo, image));
    }


  /*  @Test
    void addVehicle() {
        Assertions.assertEquals(4,bookingServiceImplementation.addVehicle(getVehiclePojo()).getSeatCapacity());
    }

    @Test
    void addVehicle_Throws_Exception(){
        VehiclePojo vehiclePojo = new VehiclePojo();
        vehiclePojo.setVehicleNumber("ABC123");
        Mockito.when(vehicleInfoRepo.getByVehicleNumber(vehiclePojo.getVehicleNumber())).thenReturn(new VehicleEntity());
        assertThrows(VehicleNumberException.class, () ->
            bookingServiceImplementation.addVehicle(vehiclePojo)
        );
    }*/

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
   /* @Test
    void updateVehicleTestWhenVehicleNotInDB() {
        when(vehicleInfoRepo.getByVehicleNumber(Mockito.anyString())).thenReturn(null);
        assertEquals(4,bookingServiceImplementation.updateVehicle(getVehiclePojo()).getSeatCapacity());
    }

    @Test
    void updateVehicleTestWhenVehicleExistsInDB() {
        when(vehicleInfoRepo.getByVehicleNumber(Mockito.anyString())).thenReturn(getVehicleEntity());
        assertEquals(4,bookingServiceImplementation.updateVehicle(getVehiclePojo()).getSeatCapacity());
    }*/

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