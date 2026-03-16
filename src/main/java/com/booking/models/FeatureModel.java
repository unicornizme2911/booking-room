package com.booking.models;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.List;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "features")
@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)
public class FeatureModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String icon;

    @ManyToMany(mappedBy = "features")
    private List<CategoryModel> categories;
}
