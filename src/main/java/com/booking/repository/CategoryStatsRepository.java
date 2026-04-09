package com.booking.repository;

import com.booking.models.CategoryModel;
import com.booking.models.CategoryStats;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface CategoryStatsRepository extends JpaRepository<CategoryStats, Long> {
    List<CategoryStats> findTop10ByOrderByScoreDesc();

    Optional<CategoryStats> findByCategory(CategoryModel category);

    @Modifying
    @Query("""
        UPDATE CategoryStats cs
        SET
            cs.total_bookings = cs.total_bookings + 1,
            cs.total_rooms_sold = cs.total_rooms_sold + :rooms
        WHERE cs.category.id = :categoryId
    """)
    void updateBooking(Long categoryId, int rooms);

    @Modifying
    @Query("""
        UPDATE CategoryStats cs
        SET
            cs.avg_rating = ((cs.avg_rating * cs.total_reviews) + :rating) / (cs.total_reviews + 1),
            cs.total_reviews = cs.total_reviews + 1
        WHERE cs.category.id = :categoryId
    """)
    void updateRating(Long categoryId, double rating);

    @Modifying
    @Query("""
        UPDATE CategoryStats cs
        SET cs.score = 
            (cs.total_bookings * 0.4) +
            (cs.total_rooms_sold * 0.3) +
            (cs.avg_rating * cs.total_reviews * 0.3)
        WHERE cs.category.id = :categoryId
    """)
    void updateScore(Long categoryId);
}
