package com.booking.models;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "categorie_images")
@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)
public class CategoryImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String image;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private CategoryModel category;
}
