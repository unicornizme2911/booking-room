package com.booking.models;

import lombok.*;
import jakarta.persistence.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "reservation_meals")
@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)
public class ReservationMeal {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "reservation_id", nullable = false)
    private ReservationModel reservation;

    @ManyToOne
    @JoinColumn(name = "meal_id", nullable = false)
    private MealModel meal;

    private int quantity;
}

