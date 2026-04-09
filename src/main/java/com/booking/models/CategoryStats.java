package com.booking.models;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "category_stats")
@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)
public class CategoryStats {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @MapsId
    @JoinColumn(name = "category_id")
    private CategoryModel category;

    private int total_bookings;
    private int total_rooms_sold;
    private double avg_rating;
    private int total_reviews;
    private double score;
}
