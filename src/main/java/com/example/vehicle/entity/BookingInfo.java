package com.example.vehicle.entity;

import com.sun.istack.NotNull;
import lombok.Data;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name="booking_info")
@Data
public class BookingInfo {
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="id")
	private Integer id;
	
	@Column(name="v_id")
	@NotNull
	private Integer vehicleId;
	
	@Column(name="date_Of_booking")
	private String dateOfBooking;
	
	@Column(name="status")
	private String status;
//	@OneToMany(targetEntity = BookingInfo.class, mappedBy = "vehicleId",
//			cascade = CascadeType.ALL, fetch = FetchType.LAZY)
//	private List<VehicleInfo> vehicleInfoList;

}
