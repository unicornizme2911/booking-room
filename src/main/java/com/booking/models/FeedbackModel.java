package com.booking.models;
import lombok.*;
import jakarta.persistence.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "feedbacks",
    uniqueConstraints = @UniqueConstraint(columnNames = {"reservation_id", "category_id"}))
@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)
public class FeedbackModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int rating;

    @Column(columnDefinition = "TEXT")
    private String comment;

    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UserModel user;

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private CategoryModel category;

    @ManyToOne
    @JoinColumn(name = "reservation_id", nullable = false)
    private ReservationModel reservation;
}
