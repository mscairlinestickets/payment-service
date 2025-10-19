package com.erickWck.payment_service.entity;

import jakarta.persistence.*;
import lombok.Builder;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Builder(toBuilder = true)
public record Payment(

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        Long id,

        Long bookId,

        String name,

        String cpfNumber,

        String cardholderName,

        String pixKey,

        BigDecimal amount,

        String type,

        String cardNumber,

        String expiryDate,

        String cvv,

        PaymentStatus status,

        PaymentType paymentType,

        @CreatedDate
        Instant createdAt,

        @LastModifiedBy
        Instant lastModifiedAt,

        @Version
        int version
) {

}
