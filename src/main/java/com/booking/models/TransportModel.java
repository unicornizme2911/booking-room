package com.booking.models;
import lombok.*;
import jakarta.persistence.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.util.*;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "transports")
@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)
public class TransportModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String vehicle;

    @Column(nullable = false)
    private BigDecimal price;

    @OneToMany(mappedBy = "transport")
    private List<ReservationModel> reservations;
}
