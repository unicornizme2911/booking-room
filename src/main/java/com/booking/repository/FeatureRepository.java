package com.booking.repository;

import com.booking.models.CategoryModel;
import com.booking.models.FeatureModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FeatureRepository extends JpaRepository<FeatureModel,Long> {

}
