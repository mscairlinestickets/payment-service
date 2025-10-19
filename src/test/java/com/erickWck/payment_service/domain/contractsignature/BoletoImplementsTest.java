package com.erickWck.payment_service.domain.contractsignature;

import com.erickWck.payment_service.entity.PaymentDtoTransaction;
import com.erickWck.payment_service.entity.PaymentStatus;
import com.erickWck.payment_service.entity.PaymentType;
import com.erickWck.payment_service.exception.LimitUnavailable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

class BoletoImplementsTest {

    private BoletoImplements boletoImplements;

    @BeforeEach
    void setup() {
        boletoImplements = new BoletoImplements();
    }

    @Test
    @DisplayName("Deve lançar uma exceção se o valor for maior que o limite e definir o status para REJECTED")
    void payWithBoletoShouldThrowExceptionAndRejectStatus() {
        //arrange
        BigDecimal limitCard = BigDecimal.valueOf(1500.90);
        var payment = getPaymentTransaction();
        payment.setAvailableAmount(limitCard);
        //act e assert
        assertThrows(LimitUnavailable.class, () -> boletoImplements.processPayment(payment));
        assertEquals(PaymentStatus.REJECTED, payment.getStatus());
    }

    @Test
    @DisplayName("Deve lançar uma exceção se o limite for negativo e definir o status para REJECTED")
    void payWithBoletoShouldThrowExceptionAndRejectStatusWhenLimitIsNegative() {
        //arrange
        BigDecimal limitCard = BigDecimal.valueOf(-1);
        var payment = getPaymentTransaction();
        payment.setAvailableAmount(limitCard);
        //act e assert
        assertThrows(LimitUnavailable.class, () -> boletoImplements.processPayment(payment));
        assertEquals(PaymentStatus.REJECTED, payment.getStatus());
    }


    @Test
    @DisplayName("Deve gerar um boleto e definir o status para aprovado")
    void payWithBoletoShouldGenerateBoletoAndApproveStatus() {
        //arrange
        BigDecimal limitCard = BigDecimal.valueOf(2600.90);
        var payment = getPaymentTransaction();
        payment.setAvailableAmount(limitCard);

        //act
        var response = boletoImplements.processPayment(payment);

        //assert
        assertEquals(PaymentStatus.APPROVED, response.getStatus());
        assertEquals(PaymentType.BOLETO, response.getPaymentType());
    }

    private static PaymentDtoTransaction getPaymentTransaction() {
        var limitCard = getCreatedLimit();
        return PaymentDtoTransaction.builder()
                .name("Erick Silva")
                .cpfNumber("79214844500")
                .bookId(1L)
                .cardholderName("João da Silva")
                .amount(BigDecimal.valueOf(1800.90))
                .type("credito")
                .cardNumber("79214844500")
                .expiryDate("122025")
                .cvv("123")
                .build();
    }

    private static BigDecimal getCreatedLimit() {
        return BigDecimal.valueOf(2000.90).setScale(2, RoundingMode.HALF_UP);
    }

}