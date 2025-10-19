package com.erickWck.payment_service.domain.repositories;

import com.erickWck.payment_service.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findByBookId(Long bookId);
}
