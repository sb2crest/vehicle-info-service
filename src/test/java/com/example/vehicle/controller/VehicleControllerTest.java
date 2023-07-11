package com.example.vehicle.controller;

import com.example.vehicle.pojo.VehiclePojo;
import com.example.vehicle.entity.VehicleEntity;
import com.example.vehicle.model.TestUtil;
import com.example.vehicle.service.VehicleService;
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

import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(VehicleController.class)
class VehicleControllerTest {
    @MockBean
    VehicleService vehicleService;
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
    void addVehicle() throws Exception {
        Mockito.when(vehicleService.addVehicle(Mockito.any())).thenReturn(getVehicleEntity());
        mvc.perform(post("/addVehicle").content(TestUtil.convertObjectToJsonBytes(getVehiclePojo()))
                        .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk());
    }
    @Test
    void addVehicleForInvalidNumber() throws Exception {
        VehiclePojo vehiclePojo = getVehiclePojo();
        vehiclePojo.setVehicleNumber("123445");
        Mockito.when(vehicleService.addVehicle(Mockito.any())).thenReturn(getVehicleEntity());
        mvc.perform(post("/addVehicle").content(TestUtil.convertObjectToJsonBytes(vehiclePojo))
                .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isBadRequest());
    }
    @Test
    void addVehicleWithNoNumber() throws Exception { //when number is null
        VehiclePojo vehiclePojo =new VehiclePojo();
        vehiclePojo.setName("audi");
        vehiclePojo.setModel("x7");
        vehiclePojo.setSeatCapacity(4);
        Mockito.when(vehicleService.addVehicle(Mockito.any())).thenReturn(getVehicleEntity());
        mvc.perform(post("/addVehicle").content(TestUtil.convertObjectToJsonBytes(vehiclePojo))
                .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isBadRequest());
    }

    @Test
    void getVehicle() throws Exception {
        Mockito.when(vehicleService.getVehicle(Mockito.any())).thenReturn(getVehiclePojo());
        mvc.perform(put("/updateVehicle").content(TestUtil.convertObjectToJsonBytes(getVehiclePojo()))
                .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk());
    }

    @Test
    void updateVehicle() throws Exception {
        Mockito.when(vehicleService.updateVehicle(Mockito.any())).thenReturn(getVehicleEntity());
        mvc.perform(put("/updateVehicle").content(TestUtil.convertObjectToJsonBytes(getVehiclePojo()))
                .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk());
    }

    @Test
    void deleteBooking() throws Exception {
        Mockito.when(vehicleService.deleteVehicle(Mockito.anyString())).thenReturn("deleted Successfully");
        mvc.perform(delete("/deleteVehicle").param("vehicleNumber","12")
                .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk());
    }
    @Test
    void deleteBookingWhenVehicleDoesNotExist() throws Exception {
        Mockito.when(vehicleService.deleteVehicle(Mockito.anyString())).thenReturn("no vehicle with this number 12");
        mvc.perform(delete("/deleteVehicle").param("vehicleNumber","12")
                .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk());
    }

    VehiclePojo getVehiclePojo(){
        VehiclePojo vehiclePojo =new VehiclePojo();
        vehiclePojo.setName("audi");
        vehiclePojo.setModel("x7");
        vehiclePojo.setSeatCapacity(4);
        vehiclePojo.setVehicleNumber("KA 12 ab 1234");
        return vehiclePojo;
    }
    VehicleEntity getVehicleEntity(){
        VehicleEntity vehicleEntity =new VehicleEntity();
        vehicleEntity.setName("audi");
        vehicleEntity.setModel("x7");
        vehicleEntity.setSeatCapacity(4);
        vehicleEntity.setVehicleNumber("12");
        return vehicleEntity;
    }
}