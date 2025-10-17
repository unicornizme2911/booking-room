package com.booking.repository;

import com.booking.models.MealModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MealRepository extends JpaRepository<MealModel,String> {
    Optional<MealModel> findByName(String name);
}
