package com.booking.repository;

import com.booking.models.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserModel,String> {
    Optional<UserModel> findUserModelById(Long id);
    Optional<UserModel> findByEmail(String email);

    boolean existsUserModelByEmail(String email);
}
