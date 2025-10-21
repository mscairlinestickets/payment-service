package com.erickWck.payment_service.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate; // Corrigido
import org.springframework.data.jpa.domain.support.AuditingEntityListener; // Necessário para Auditing

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "tb_payment")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long bookId;

    private String name;

    private String cpfNumber;

    private String cardholderName;

    private String pixKey;

    private BigDecimal amount;

    private String type;

    private String cardNumber;
    private String expiryDate;
    private String cvv;
    private String codeBoleto;

    @Enumerated(EnumType.STRING)
    private PaymentStatus status;

    @Enumerated(EnumType.STRING)
    private PaymentType paymentType;

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant lastModifiedAt;

    @Version
    private int version;
}