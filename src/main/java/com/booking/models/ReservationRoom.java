package com.booking.models;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "reservation_rooms")
@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)
public class ReservationRoom {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private BigDecimal price_at_booking;

    @ManyToOne
    @JoinColumn(name = "reservation_id")
    private ReservationModel reservation;

    @ManyToOne
    @JoinColumn(name = "room_id")
    private RoomModel room;
}