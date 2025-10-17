package com.booking.repository;

import com.booking.models.CategoryModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CategoryRepository extends JpaRepository<CategoryModel,String> {
    Optional<CategoryModel> findByName(String name);
}
