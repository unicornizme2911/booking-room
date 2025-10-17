package com.booking.repository;

import com.booking.models.TokenModel;
import com.booking.models.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface TokenRepository extends JpaRepository<TokenModel, String> {
    @Query("SELECT t FROM TokenModel t WHERE t.users.id = ?1 AND (t.expired = false OR t.revoked = false)")
    List<TokenModel> findAllValidTokenByUser(String id);

    @Query("SELECT t FROM TokenModel t WHERE t.users.id = ?1")
    List<TokenModel> findAllByUsersId(String id);

    Optional<TokenModel> findByToken(String token);

    void deleteTokenModelByUsers(UserModel user);

    Optional<TokenModel> findByUsers(UserModel user);

    List<TokenModel> findAllByUsers(UserModel user);
}
