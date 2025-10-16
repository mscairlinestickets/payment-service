package com.erickWck.payment_service.domain.contractsignature;

import com.erickWck.payment_service.entity.PaymentDtoTransaction;
import com.erickWck.payment_service.entity.PaymentStatus;
import com.erickWck.payment_service.exception.LimitUnavailable;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static org.junit.Assert.*;

public class BoletoImplementsTest {

    @DisplayName("Compra aprovada atualizando o Status para aprovado.")
    @Test
    void limitCard() {
        //arrange
        CreditCardImplements limit = new CreditCardImplements();
        BigDecimal limitCard = getCreatedLimit();

        PaymentDtoTransaction card = PaymentDtoTransaction.builder()
                .name("Erick Silva")
                .cpfNumber("79214844500")
                .bookId(1L)
                .cardholderName("João da Silva")
                .amount(BigDecimal.valueOf(1800.90))
                .type("credito")
                .cardNumber("79214844500")
                .expiryDate("122025")
                .cvv("123")
                .availableAmount(limitCard)
                .build();

        //act
        limit.payWithCreditCard(card);
        // assert
        assertNotEquals(limitCard, card.getAmount());
        assertEquals(PaymentStatus.APPROVED, card.getStatus());
    }

    @DisplayName("Compra aprovada valor da compra e limite sejam iguais atualizando o Status para aprovado.")
    @Test
    void shouldApprovedAmmountAndAvaliableAmountIsEquals() {
        //arrange
        CreditCardImplements limit = new CreditCardImplements();
        BigDecimal limitCard = BigDecimal.valueOf(3500.50).setScale(2, RoundingMode.HALF_UP);

        PaymentDtoTransaction card = PaymentDtoTransaction.builder()
                .amount(limitCard)
                .availableAmount(limitCard)
                .build();

        //act
        limit.payWithCreditCard(card);

        // assert
        assertEquals(PaymentStatus.APPROVED, card.getStatus());
    }

    @DisplayName("Compra rejeitada quando valor é maior que o limite.")
    @Test
    void shouldRejectWhenAmountIsGreaterThanLimit() {
        //arrange
        CreditCardImplements limit = new CreditCardImplements();

        BigDecimal limitCard = BigDecimal.valueOf(200.90).setScale(2, RoundingMode.HALF_UP);

        PaymentDtoTransaction card = PaymentDtoTransaction.builder()
                .amount(BigDecimal.valueOf(3500.50))
                .availableAmount(limitCard)
                .build();

        //act eassert
        assertThrows(LimitUnavailable.class, () -> limit.payWithCreditCard(card));
        assertEquals(PaymentStatus.REJECTED, card.getStatus());
    }

    @DisplayName("Compra rejeitada quando limite é negativo.")
    @Test
    void limitIsSmallerThanZero() {
        //arrange
        CreditCardImplements limit = new CreditCardImplements();

        BigDecimal limitCard = BigDecimal.valueOf(-1);

        PaymentDtoTransaction card = PaymentDtoTransaction.builder()
                .amount(BigDecimal.valueOf(100))
                .availableAmount(limitCard)
                .build();

        //act eassert
        assertThrows(LimitUnavailable.class, () -> limit.payWithCreditCard(card));
        assertEquals(PaymentStatus.REJECTED, card.getStatus());
    }

    private static BigDecimal getCreatedLimit() {
        BigDecimal limitCard = BigDecimal.valueOf(2000.90).setScale(2, RoundingMode.HALF_UP);
        return limitCard;
    }


}
