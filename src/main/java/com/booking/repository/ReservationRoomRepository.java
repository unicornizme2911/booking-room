package com.booking.repository;

import com.booking.models.ReservationRoom;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReservationRoomRepository extends JpaRepository<ReservationRoom,String> {

}
