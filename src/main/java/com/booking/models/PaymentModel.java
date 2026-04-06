package com.booking.models;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "payments")
@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)
public class PaymentModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String txnRef;

    private Long total;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus status;

    private String provider;

    private String transactionNo;

    private LocalDateTime createdAt;

    private LocalDateTime paidAt;

    @OneToOne
    @JoinColumn(name = "reservation_id", nullable = false)
    private ReservationModel reservation;
}
