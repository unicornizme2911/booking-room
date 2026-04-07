package com.booking.repository;

import com.booking.models.ReservationModel;
import com.booking.models.ReservationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

public interface ReservationRepository extends JpaRepository<ReservationModel,String> {
    @Query(value = """
        SELECT * FROM reservations r
        WHERE (r.checkIn <= :toDate AND r.checkOut >= :fromDate)
    """, nativeQuery = true)
    List<ReservationModel> findOverlappingReservations(@Param("fromDate") Date fromDate, @Param("toDate") Date toDate);

    @Query("""
        SELECT COUNT(rr) > 0
        FROM ReservationRoom rr
        JOIN rr.reservation res
        WHERE rr.room.id = :roomId
        AND res.check_in < :toDate
        AND res.check_out > :fromDate
        AND res.status IN ('CONFIRMED', 'HOLD')
    """)
    boolean existsConflict(Long roomId, Date fromDate, Date toDate);

    List<ReservationModel> findByStatusAndExpiredAtBefore(ReservationStatus status, LocalDateTime time);
}
