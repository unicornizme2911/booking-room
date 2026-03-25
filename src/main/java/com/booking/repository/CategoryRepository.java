package com.booking.repository;

import com.booking.dto.response.CategoryAvailableResponse;
import com.booking.models.CategoryModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<CategoryModel,String> {
    Optional<CategoryModel> findByName(String name);

    @Query("""
            SELECT new com.booking.dto.response.CategoryAvailableResponse(c.id, COUNT(r.id))
            FROM CategoryModel c
            JOIN c.rooms r
            WHERE c.capacity >= :capacity
            AND NOT EXISTS (
                 SELECT 1
                 FROM ReservationRoom rr
                 JOIN rr.reservation res
                 WHERE rr.room.id = r.id
                 AND res.check_in < :toDate
                 AND res.check_out > :fromDate
                 AND res.status IN ('CONFIRMED','HOLD')
            )
            GROUP BY c.id, c.name
            """)
    List<CategoryAvailableResponse> countRoomAvailable(Date fromDate, Date toDate, int capacity);
}
