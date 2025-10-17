package com.booking.repository;

import com.booking.models.ReservationModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

public interface ReservationRepository extends JpaRepository<ReservationModel,String> {
    @Query(value = "SELECT * FROM reservations r "
            + "WHERE (r.checkIn <= :toDate AND r.checkOut >= :fromDate)", nativeQuery = true)
    List<ReservationModel> findOverlappingReservations(@Param("fromDate") Date fromDate, @Param("toDate") Date toDate);
}
