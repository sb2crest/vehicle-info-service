package com.example.vehicle.repository;

import com.example.vehicle.entity.BookingInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookingInfoRepo extends JpaRepository<BookingInfo,Integer> {
}
