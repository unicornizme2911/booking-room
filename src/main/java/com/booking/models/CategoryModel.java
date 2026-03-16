package com.booking.models;

import lombok.*;
import jakarta.persistence.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import java.util.*;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "categories")
@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)
public class CategoryModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private int capacity;

    @Column(nullable = false)
    private double price;

    @Column(nullable = false)
    private String bed_type;

    @Column(nullable = false)
    private int room_size;

    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL)
    @Builder.Default
    private List<CategoryImage> images = new ArrayList<>();

    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL)
    private List<RoomModel> rooms;

    @ManyToMany
    @JoinTable(
            name = "category_features",
            joinColumns = @JoinColumn(name = "category_id"),
            inverseJoinColumns = @JoinColumn(name = "feature_id")
    )
    private List<FeatureModel> features;
}
