package com.booking.repository;

import com.booking.models.FeedbackModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

interface FeedbackRepository extends JpaRepository<FeedbackModel, Long> {
    boolean existsByReservationIdAndCategoryId(Long reservationId, Long categoryId);

    List<FeedbackModel> findByCategoryId(Long categoryId);
}
