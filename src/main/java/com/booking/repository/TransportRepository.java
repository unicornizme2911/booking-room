package com.booking.repository;

import com.booking.models.TransportModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TransportRepository extends JpaRepository<TransportModel,String> {
    Optional<TransportModel> findByVehicle(String vehicle);
}
