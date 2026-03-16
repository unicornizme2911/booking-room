package com.booking.repository;

import com.booking.models.RoomModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface RoomRepository extends JpaRepository<RoomModel,String> {
    List<RoomModel> findAllByCategory_Id(Long categoryId);

    RoomModel findByRoomNumber(String roomNumber);

    @Query("""
    SELECT r
    FROM RoomModel r
    WHERE r.id NOT IN (
        SELECT rr.room.id
        FROM ReservationRoom rr
        JOIN rr.reservation res
        WHERE res.check_in < :toDate
        AND res.check_out > :fromDate
        AND res.status = 'CONFIRMED'
    )
    """)
    List<RoomModel> findAvailableRooms(Date fromDate, Date toDate);
}
