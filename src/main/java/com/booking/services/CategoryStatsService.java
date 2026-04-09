package com.booking.services;

import com.booking.dto.response.CategoryStatsResponse;
import com.booking.models.CategoryModel;
import com.booking.models.CategoryStats;
import com.booking.repository.CategoryRepository;
import com.booking.repository.CategoryStatsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CategoryStatsService {
    @Autowired
    private CategoryStatsRepository categoryStatsRepository;
    @Autowired
    private CategoryRepository categoryRepository;

    public CategoryStatsResponse toResponse(CategoryStats categoryStats) {
        return CategoryStatsResponse.builder()
                .id(categoryStats.getCategory().getId())
                .name(categoryStats.getCategory().getName())
                .total_bookings(categoryStats.getTotal_bookings())
                .total_reviews(categoryStats.getTotal_reviews())
                .total_rooms_sold(categoryStats.getTotal_rooms_sold())
                .avg_rating(categoryStats.getAvg_rating())
                .score(categoryStats.getScore())
                .build();
    }

    public List<CategoryStatsResponse> toResponse(List<CategoryStats> categoryStatsList) {
        return categoryStatsList.stream().map(this::toResponse).toList();
    }

    public CategoryStats getOrCreate(Long id) {
        var category = categoryRepository.findById(String.valueOf(id)).orElseThrow();
        return categoryStatsRepository.findByCategory(category).orElseGet(() -> {
            var categoryStats = CategoryStats.builder()
                    .category(category)
                    .total_bookings(0)
                    .total_reviews(0)
                    .total_rooms_sold(0)
                    .avg_rating(0)
                    .score(0)
                    .build();
            categoryStatsRepository.save(categoryStats);
            return categoryStats;
        });
    }

    public void updateBooking(Long category_id, int rooms) {
        CategoryStats stats = getOrCreate(category_id);
        var id = stats.getCategory().getId();
        categoryStatsRepository.updateBooking(id, rooms);
        categoryStatsRepository.updateScore(id);
    }

    public void updateRating(Long category_id, double new_rating) {
        CategoryStats stats = getOrCreate(category_id);
        var id = stats.getCategory().getId();
        categoryStatsRepository.updateRating(id, new_rating);
        categoryStatsRepository.updateScore(id);
    }

    @Transactional(readOnly = true)
    public List<CategoryStatsResponse> getTopCategories() {
        return toResponse(categoryStatsRepository.findTop10ByOrderByScoreDesc());
    }
}
