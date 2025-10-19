package com.erickWck.payment_service.domain.contractsignature;

import com.erickWck.payment_service.entity.PaymentDtoTransaction;
import com.erickWck.payment_service.entity.PaymentStatus;
import com.erickWck.payment_service.entity.PaymentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class PixImplementsTest {

    private PixImplements pixImplements;

    @BeforeEach
    void setup() {
        pixImplements = new PixImplements();
    }

    @Test
    @DisplayName("Deve processar o pagamento Pix com sucesso e definir o status como APPROVED")
    void payWithPixShouldProcessSuccessfullyAndSetStatusApproved() {
        // Arrange
        PaymentDtoTransaction transaction = PaymentDtoTransaction.builder()
                .bookId(1L)
                .name("Erick Silva")
                .amount(BigDecimal.valueOf(150.75))
                .pixKey("chavepixaleatoria123")
                .build();

        // Act
        pixImplements.processPayment(transaction);

        // Assert
        assertEquals(PaymentStatus.APPROVED, transaction.getStatus());
        assertEquals(PaymentType.PIX, transaction.getPaymentType());
    }

    @Test
    @DisplayName("Deve lançar IllegalArgumentException se o valor do Pix for zero ou negativo")
    void payWithPixShouldThrowExceptionIfAmountIsZeroOrNegative() {
        // Arrange
        PaymentDtoTransaction transactionZero = PaymentDtoTransaction.builder()
                .bookId(2L)
                .name("Cliente Teste")
                .amount(BigDecimal.ZERO)
                .pixKey("chavepixzero")
                .build();

        PaymentDtoTransaction transactionNegative = PaymentDtoTransaction.builder()
                .bookId(3L)
                .name("Cliente Teste")
                .amount(BigDecimal.valueOf(-10.00))
                .pixKey("chavepixnegativa")
                .build();

        // Act & Assert
        IllegalArgumentException exceptionZero = assertThrows(IllegalArgumentException.class, () -> pixImplements.processPayment(transactionZero));
        assertEquals("Valor de pagamento Pix deve ser maior que zero.", exceptionZero.getMessage());

        IllegalArgumentException exceptionNegative = assertThrows(IllegalArgumentException.class, () -> pixImplements.processPayment(transactionNegative));
        assertEquals("Valor de pagamento Pix deve ser maior que zero.", exceptionNegative.getMessage());
    }

    @Test
    @DisplayName("Deve definir o status como PENDING inicialmente e depois APPROVED")
    void payWithPixShouldSetStatusPendingThenApproved() {
        // Arrange
        PaymentDtoTransaction transaction = PaymentDtoTransaction.builder()
                .bookId(1L)
                .name("Erick Silva")
                .amount(BigDecimal.valueOf(150.75))
                .pixKey("chavepixaleatoria123")
                .status(PaymentStatus.PENDING)
                .build();
        // Act
        var response = pixImplements.processPayment(transaction);
        // Assert
        assertEquals(PaymentStatus.APPROVED, response.getStatus());
        assertEquals(PaymentType.PIX, response.getPaymentType());
    }

    @Test
    void payWithPixShouldThrowExceptionIfAmountIsNull(){
        //arrange
        PaymentDtoTransaction transaction = PaymentDtoTransaction.builder()
                .bookId(1L)
                .name("Erick Silva")
                .pixKey("chavepixaleatoria123")
                .status(PaymentStatus.PENDING)
                .build();
        //act e assert
        assertThrows(IllegalArgumentException.class, ()-> pixImplements.processPayment(transaction));

    }

    @Test
    void payWithPixShouldThrowExceptionIfAmountIsNegative(){
        //arrange
        PaymentDtoTransaction transaction = PaymentDtoTransaction.builder()
                .bookId(1L)
                .name("Erick Silva")
                .pixKey("chavepixaleatoria123")
                .amount(BigDecimal.valueOf(-5))
                .status(PaymentStatus.PENDING)
                .build();
        //act e assert
        assertThrows(IllegalArgumentException.class, ()-> pixImplements.processPayment(transaction));

    }

}