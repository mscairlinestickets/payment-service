package com.erickWck.payment_service.domain.validator;

import com.erickWck.payment_service.domain.CardTransaction;
import com.erickWck.payment_service.domain.PaymentStatus;
import com.erickWck.payment_service.domain.exception.LimitUnavailable;
import com.erickWck.payment_service.validator.LimitCreditCard;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ValidCreditCardTest {

    @Test
    void limitCard() {
        //arrange
        LimitCreditCard limit = new LimitCreditCard();
        BigDecimal limitCard = limit.createLimitCard();

        CardTransaction card = CardTransaction.builder()
                .name("Erick Silva")
                .cpfNumber("79214844500")
                .paymentId(98765L)
                .bookId(1L)
                .cardholderName("João da Silva")
                .amount(BigDecimal.valueOf(2599.90))
                .type("credito")
                .cardNumber("79214844500")
                .expiryDate("122025")
                .cvv("123")
                .limit(limitCard)
                .build();

        //act e assert
        assertEquals(limitCard, card.getLimit());
    }

    @Test
    void limitIsBiggerIfPaymentIsCredit() {
        //arrange
        LimitCreditCard limit = new LimitCreditCard();

        BigDecimal limitCard = BigDecimal.valueOf(2000.90).setScale(2, RoundingMode.HALF_UP);

        CardTransaction card = CardTransaction.builder()
                .name("Erick Silva")
                .cpfNumber("79214844500")
                .paymentId(98765L)
                .bookId(1L)
                .cardholderName("João da Silva")
                .amount(BigDecimal.valueOf(2599.90))
                .type("credito")
                .cardNumber("79214844500")
                .expiryDate("122025")
                .cvv("123")
                .limit(limitCard)
                .status(PaymentStatus.APPROVED)
                .build();

        //act eassert
        assertThrows(LimitUnavailable.class, () -> limit.limitPaymentCreditCard(card, limitCard.doubleValue()));
        assertEquals(PaymentStatus.REJECTED, card.getStatus());
    }



}
