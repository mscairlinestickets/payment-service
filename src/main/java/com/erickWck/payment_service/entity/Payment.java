package com.erickWck.payment_service.entity;

import lombok.Builder;

import java.math.BigDecimal;
import java.time.Instant;

@Builder(toBuilder = true)
public record Payment(

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

        Instant createdAt,

        Instant lastModifiedAt,

        int version
) {

}
