package com.booking.models;

import lombok.*;
import jakarta.persistence.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.*;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "rooms")
@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)
public class RoomModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, name = "room_number")
    private String roomNumber;

    @Column(nullable = false, name = "room_floor")
    private int roomFloor;

    @Column(nullable = false)
    private Double price;

    @Column(nullable = false)
    private String status;

    @Column(name = "image_path")
    private String imagePath;

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private CategoryModel category;

    @ManyToMany(mappedBy = "rooms")
    private List<ReservationModel> reservations;
}
