package com.booking.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CategoryStatsResponse {
    private Long id;
    private String name;
    private double avg_rating;
    private int total_bookings;
    private int total_reviews;
    private int total_rooms_sold;
    private double score;
}
