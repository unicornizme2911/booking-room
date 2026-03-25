package com.booking.models;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import jakarta.persistence.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.*;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "reservations")
@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)
public class ReservationModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Date check_in;
    private Date check_out;

    @Column(nullable = false)
    private String status;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime expiredAt;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserModel user;

    @ManyToOne
    @JoinColumn(name = "transport_id")
    private TransportModel transport;

    @OneToMany(mappedBy = "reservation", cascade = CascadeType.ALL)
    private List<ReservationRoom> reservation_rooms = new ArrayList<>();

    @OneToMany(mappedBy = "reservation", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReservationMeal> reservation_meals;

    @OneToOne(mappedBy = "reservation", cascade = CascadeType.ALL)
    private InvoiceModel invoice;
}
