package com.booking.repository;

import com.booking.models.RoomModel;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

public interface RoomRepository extends JpaRepository<RoomModel,String> {
    List<RoomModel> findAllByCategory_Id(Long categoryId);

    RoomModel findByRoomNumber(String roomNumber);

    @Query(value = """
        SELECT *
        FROM rooms r
        WHERE r.category_id = :categoryId
        AND NOT EXISTS (
            SELECT 1
            FROM reservation_rooms rr
            JOIN reservations res ON res.id = rr.reservation_id
            WHERE rr.room_id = r.id
            AND res.check_in < :toDate
            AND res.check_out > :fromDate
            AND res.status IN ('CONFIRMED', 'HOLD')
        )
        ORDER BY RAND()
        LIMIT :quantity
    """, nativeQuery = true)
    List<RoomModel> findRandomAvailableRooms(
            Long categoryId,
            Date fromDate,
            Date toDate,
            int quantity
    );

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query(value = "SELECT r FROM RoomModel r WHERE r.id IN (:roomIds)")
    List<RoomModel> lockRooms(@Param("roomIds") List<Long> roomIds);
}
