package com.erickWck.payment_service.domain.service;

import com.erickWck.payment_service.domain.contractsignature.PixImplements;
import com.erickWck.payment_service.domain.repositories.PaymentRepository;
import com.erickWck.payment_service.entity.Payment;
import com.erickWck.payment_service.entity.PaymentDtoTransaction;
import com.erickWck.payment_service.entity.PaymentStatus;
import com.erickWck.payment_service.entity.PaymentType;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PaymentServiceTest {

    @Mock
    private PixImplements pixImplements;

    @Mock
    private PaymentRepository paymentRepository;

    @InjectMocks
    private PixService pixService;

    @Nested
    class verifyTypeIsPix {

        @Test
        void shouldTypeEqualsPix() {
            //arrange
            var payment = createPayment();
            var responsePayment = createResponseDtoPayment();
            when(pixImplements.payWithPix(any())).thenReturn(responsePayment);

            //act
            var response = pixService.payIfTypePix(payment);

            //assert
            assertEquals(PaymentStatus.APPROVED, response.getStatus());
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
        }

    }

    @Nested
    class savePaymentTypePix {

        @Test
        void shouldSavePaymentWhenTypeIsPix() {
            //arrange
            var payment = createPayment();
            var responsePayment = createResponsePayment();
            when(paymentRepository.save(any())).thenReturn(responsePayment);

            //act
            var response = pixService.createPayment(payment);
            //assert

            assertEquals(payment.getBookId(), response.bookId());
            assertEquals(payment.getName(), response.name());
            assertEquals(payment.getPixKey(), response.pixKey());
            assertEquals(payment.getAmount(), response.amount());
            assertEquals(PaymentStatus.APPROVED, response.status());

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
