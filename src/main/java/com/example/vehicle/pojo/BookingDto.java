package com.example.vehicle.pojo;

import lombok.Data;

@Data
public class BookingDto {
	
	private Integer id;
	
	private Integer vId;
	
	private String dateOfBooking;
	
	private String status;
}
