package com.example.vehicle.service;

import com.example.vehicle.exception.VehicleNumberException;
import com.example.vehicle.pojo.VehiclePojo;
import com.example.vehicle.entity.VehicleEntity;
import com.example.vehicle.repository.VehicleInfoRepo;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(SpringExtension.class)
class VehicleServiceImplementationTest {

    @Mock
    VehicleInfoRepo vehicleInfoRepo;

    @InjectMocks
    VehicleServiceImplementation bookingServiceImplementation;

    @Test
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

    }

    @Test
    void getVehicle(){
        Mockito.when(vehicleInfoRepo.getByVehicleNumber(Mockito.anyString())).thenReturn(getVehicleEntity());
        Assertions.assertEquals(4,bookingServiceImplementation.getVehicle("12").getSeatCapacity());
    }
    @Test
    void getVehicleWHenNoMatchingRecord(){
        Mockito.when(vehicleInfoRepo.getByVehicleNumber(Mockito.anyString())).thenReturn(null);
        Assertions.assertNull(bookingServiceImplementation.getVehicle("12"));
    }
    @Test
    void updateVehicleTestWhenVehicleNotInDB() {
        Mockito.when(vehicleInfoRepo.getByVehicleNumber(Mockito.anyString())).thenReturn(null);
        Assertions.assertEquals(4,bookingServiceImplementation.updateVehicle(getVehiclePojo()).getSeatCapacity());
    }

    @Test
    void updateVehicleTestWhenVehicleExistsInDB() {
        Mockito.when(vehicleInfoRepo.getByVehicleNumber(Mockito.anyString())).thenReturn(getVehicleEntity());
        Assertions.assertEquals(4,bookingServiceImplementation.updateVehicle(getVehiclePojo()).getSeatCapacity());
    }

    @Test
    void deleteBooking() {
        Mockito.when(vehicleInfoRepo.getByVehicleNumber(Mockito.anyString())).thenReturn(getVehicleEntity());
        Assertions.assertEquals("deleted Successfully",bookingServiceImplementation.deleteVehicle("12"));
    }

    @Test
    void deleteBookingForNull() {
        Mockito.when(vehicleInfoRepo.getByVehicleNumber(Mockito.anyString())).thenReturn(null);
        Assertions.assertEquals("no vehicle with this number 12",bookingServiceImplementation.deleteVehicle("12"));
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