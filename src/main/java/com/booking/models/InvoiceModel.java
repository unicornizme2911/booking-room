package com.booking.models;
import lombok.*;
import jakarta.persistence.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import java.util.*;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "invoices")
@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)
public class InvoiceModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Date created_date;

    private Double total;

    @OneToOne
    @JoinColumn(name = "reservation_id", nullable = false)
    private ReservationModel reservation;
}
