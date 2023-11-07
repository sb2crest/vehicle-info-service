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
import org.mockito.*;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
        MockMultipartFile mockMultipartFile = new MockMultipartFile("file", "testImage.jpg", "image/jpeg", "your file content".getBytes());
        when(vehicleInfoRepo.getByVehicleNumber(any())).thenReturn(null);
        List<String> s3ImageUrl = Collections.singletonList("https://s3.example.com/"+vehiclePojo.getVehicleNumber()+"_image1.jpg");
        when(amazonS3.getUrl(anyString(), anyString())).thenAnswer(invocation -> {
            String key = invocation.getArgument(1);
            return new URL("https://s3.example.com/" + key);
        });
        VehicleEntity result = bookingServiceImplementation.addVehicle(vehiclePojo, Collections.singletonList(mockMultipartFile));
        assertEquals(vehiclePojo.getVehicleNumber(), result.getVehicleNumber());
        assertEquals(vehiclePojo.getSeatCapacity(), result.getSeatCapacity());
        assertEquals(s3ImageUrl, result.getS3ImageUrl());
        verify(vehicleInfoRepo, times(1)).getByVehicleNumber(any());
    }


    @Test()
    void testAddVehicle_DuplicateNumber() {
        VehiclePojo vehiclePojo = getVehiclePojo();
        MultipartFile image = new MockMultipartFile("testImage.jpg", "testImage.jpg", "image/jpeg", "test".getBytes());
        when(vehicleInfoRepo.getByVehicleNumber(any())).thenReturn(new VehicleEntity());
        assertThrows(VehicleNumberException.class, () ->bookingServiceImplementation.addVehicle(vehiclePojo, Collections.singletonList(image)));
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
        existingEntity.setS3ImageUrl(Collections.singletonList("s3://bucket/old-image.jpg"));
        when(vehicleInfoRepo.getByVehicleNumber("ABC123")).thenReturn(existingEntity);
        doNothing().when(amazonS3).deleteObject(anyString(), anyString());
        when(amazonS3.getUrl(anyString(), anyString())).thenReturn(new URL("https://bucket.s3.amazonaws.com/image.jpg"));
        when(vehicleInfoRepo.save(any(VehicleEntity.class))).thenReturn(existingEntity);
        VehicleEntity result = bookingServiceImplementation.updateVehicle(vehiclePojo, Collections.singletonList(image));
        assertNotNull(result);
        assertEquals("12", result.getVehicleNumber());
        assertEquals("[https://bucket.s3.amazonaws.com/image.jpg]", result.getS3ImageUrl().toString());
    }

    @Test
    void testUpdateVehicle_WhichDoesNotExist() throws IOException {
        VehicleEntity existingEntity = getVehicleEntity();
        MultipartFile image = new MockMultipartFile("image.jpg", new byte[0]);
        existingEntity.setS3ImageUrl(Collections.singletonList("s3://bucket/old-image.jpg"));
        String s3ImageUrl = "https://s3.example.com/testImage.jpg";
        VehiclePojo vehiclePojo = getVehiclePojo();
        when(amazonS3.getUrl(any(), any())).thenReturn(new URL(s3ImageUrl));
        when(vehicleInfoRepo.getByVehicleNumber("12")).thenReturn(existingEntity);
        VehicleEntity result = bookingServiceImplementation.updateVehicle(vehiclePojo, Collections.singletonList(image));
        assertNull(result);
        verify(vehicleInfoRepo, times(1)).getByVehicleNumber(any());
    }

    @Test
    void testExtractS3KeyFromUrl() {
        List<String> s3Url = Collections.singletonList("https://bucket.s3.amazonaws.com/image.jpg");
        String result = bookingServiceImplementation.extractS3KeyFromUrl(s3Url).toString();
        assertEquals("[image.jpg]", result);
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
    @Test
    public void testListAllVehicles_Success() {
        List<VehicleEntity> vehicleEntityList = new ArrayList<>();
        VehicleEntity entity1 = getVehicleEntity();
        entity1.setS3ImageUrl(Collections.singletonList("image1.jpg"));

        VehicleEntity entity2 = new VehicleEntity();
        entity2.setVehicleNumber("XYZ789");
        entity2.setSeatCapacity(6);
        entity2.setIsVehicleAC(false);
        entity2.setIsVehicleSleeper(false);
        entity2.setS3ImageUrl(Collections.singletonList("image2.jpg"));

        vehicleEntityList.add(entity1);
        vehicleEntityList.add(entity2);
        when(vehicleInfoRepo.findAll()).thenReturn(vehicleEntityList);
        List<VehiclePojo> result = bookingServiceImplementation.listAllVehicles();
        assertEquals(2, result.size());

        VehiclePojo pojo1 = result.get(0);
        assertEquals("12", pojo1.getVehicleNumber());
        assertEquals(4, pojo1.getSeatCapacity());
        assertEquals(true, pojo1.getIsVehicleAC());
        assertEquals(true, pojo1.getIsVehicleSleeper());
        assertEquals(Collections.singletonList("image1.jpg"), pojo1.getImageUrl());

        VehiclePojo pojo2 = result.get(1);
        assertEquals("XYZ789", pojo2.getVehicleNumber());
        assertEquals(6, pojo2.getSeatCapacity());
        assertEquals(false, pojo2.getIsVehicleAC());
        assertEquals(false, pojo2.getIsVehicleSleeper());
        assertEquals(Collections.singletonList("image2.jpg"), pojo2.getImageUrl());
    }

    @Test
    public void testListAllVehicles_Exception() {
        when(vehicleInfoRepo.findAll()).thenThrow(new RuntimeException("Simulated exception"));
        List<VehiclePojo> result = bookingServiceImplementation.listAllVehicles();
        assertEquals(0, result.size());
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
        vehicleEntity.setIsVehicleAC(true);
        vehicleEntity.setIsVehicleSleeper(true);
        return vehicleEntity;
    }
}