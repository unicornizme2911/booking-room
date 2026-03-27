package com.booking.repository;

import com.booking.models.PaymentModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentRepository extends JpaRepository<PaymentModel,String> {
    Optional<PaymentModel> findByTxnRef(String txnRef);
}
