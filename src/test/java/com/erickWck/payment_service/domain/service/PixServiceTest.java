package com.erickWck.payment_service.domain.service;

import com.erickWck.payment_service.domain.contractsignature.PixImplements;
import com.erickWck.payment_service.domain.repositories.PaymentRepository;
import com.erickWck.payment_service.entity.Payment;
import com.erickWck.payment_service.entity.PaymentDtoTransaction;
import com.erickWck.payment_service.entity.PaymentStatus;
import com.erickWck.payment_service.entity.PaymentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PixServiceTest {

    @Mock
    private PixImplements pixImplements;

    @Mock
    private PaymentRepository paymentRepository;

    @InjectMocks
    private PixService pixService;

    @Nested
    class verifyTypeIsPix {

        @Test
        @DisplayName("Deve verificar se o pagamento é do tipo PIX e o SATUS inicial é PENDING.")
        void shouldTypeEqualsPix() {
            //arrange
            var payment = createPayment();
            var responsePayment = createResponseDtoPayment();
            when(pixImplements.processPayment(any())).thenReturn(responsePayment);

            //act
            var response = pixService.payIfTypePix(payment);

            //assert
            assertEquals(PaymentStatus.PENDING, response.getStatus());
            assertEquals(PaymentType.PIX, response.getPaymentType());
            assertEquals(payment.getBookId(), response.getBookId());
        }

        @Test
        @DisplayName("Deve verificar se o pagamento é do tipo PIX e o SATUS inicial é PENDING.")
        void shouldTypeEqualsPix2() {
            //arrange
            var payment = createPayment();
            var responsePayment = createResponseDtoPayment();
            when(pixImplements.processPayment(any())).thenReturn(responsePayment);

            //act
            var response = pixImplements.processPayment(payment);

            //assert
            assertEquals(PaymentStatus.PENDING, response.getStatus());
            assertEquals(PaymentType.PIX, response.getPaymentType());
            assertEquals(payment.getBookId(), response.getBookId());
        }

        @Test
        void shouldReturnNullWhenTypeIsNotPix() {
            //arrange
            var payment = createPayment();
            payment.setType("DEBIT_CARD");

            //act
            var response = pixService.verifyTypeIsPix(payment);

            //assert
            assertNull(response);
            verify(paymentRepository, never()).save(any());
        }

    }

    @Nested
    class savePaymentTypePix {

        @Test
        void shouldSavePaymentWhenDoesNotExist() {
            //arrange
            var payment = createPayment();
            var responsePayment = createResponseDtoPayment();
            var savePayment = createResponsePayment();

            when(paymentRepository.findByBookId(payment.getBookId())).thenReturn(Optional.empty());
            when(paymentRepository.save(any())).thenReturn(savePayment);

            //act
            var response = pixService.createPayment(payment);
            //assert

            assertEquals(payment.getBookId(), response.bookId());
            assertEquals(payment.getName(), response.name());
            assertEquals(payment.getPixKey(), response.pixKey());
            assertEquals(payment.getAmount(), response.amount());
            assertEquals(PaymentStatus.APPROVED, response.status());
            verify(paymentRepository, times(1)).save(any());

        }

        @Test
        @DisplayName("Deve cobrir a verificação do tipo PIX dentro de verifyTypeIsPix")
        void shouldExecuteVerifyTypeIsPixWhenTypeIsPIX() {
            // arrange
            var payment = createPayment();
            var responsePayment = createResponseDtoPayment();
            responsePayment.setType("PIX");
            when(pixImplements.processPayment(any())).thenReturn(responsePayment);
            when(paymentRepository.findByBookId(anyLong())).thenReturn(Optional.empty());

            // act
            var response = pixService.verifyTypeIsPix(payment);

            // assert
            assertEquals(PaymentStatus.PENDING, response.getStatus());
            assertEquals("PIX", response.getType());
            verify(paymentRepository, times(1)).save(any());
        }


        @Test
        void shouldReturnExistingPaymentWhenExists() {
            //arrange
            var payment = createPayment();
            var responsePayment = createResponsePayment();
            when(paymentRepository.findByBookId(anyLong())).thenReturn(Optional.of(responsePayment));

            //act
            var result = pixService.createPayment(payment);

            //assert
            assertEquals(payment.getBookId(), result.bookId());
            assertEquals(payment.getName(), result.name());
            assertEquals(payment.getPixKey(), result.pixKey());
            assertEquals(payment.getAmount(), result.amount());
            assertEquals(PaymentStatus.APPROVED, result.status());
            verify(paymentRepository, never()).save(any());
        }

    }

    private static PaymentDtoTransaction createPayment() {
        return PaymentDtoTransaction.builder()
                .bookId(1L)
                .name("Erick Silva")
                .amount(BigDecimal.valueOf(150.75))
                .pixKey("chavepixaleatoria123")
                .status(PaymentStatus.PENDING)
                .type("PIX")
                .build();
    }

    private static PaymentDtoTransaction createResponseDtoPayment() {
        return PaymentDtoTransaction.builder()
                .bookId(1L)
                .name("Erick Silva")
                .amount(BigDecimal.valueOf(150.75))
                .pixKey("chavepixaleatoria123")
                .status(PaymentStatus.PENDING)
                .paymentType(PaymentType.PIX)
                .build();
    }

    private static Payment createResponsePayment() {
        return Payment.builder()
                .bookId(1L)
                .name("Erick Silva")
                .amount(BigDecimal.valueOf(150.75))
                .pixKey("chavepixaleatoria123")
                .status(PaymentStatus.APPROVED)
                .paymentType(PaymentType.PIX)
                .build();
    }

}
