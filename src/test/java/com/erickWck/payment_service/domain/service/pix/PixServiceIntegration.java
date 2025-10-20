package com.erickWck.payment_service.domain.service.pix;


import com.erickWck.payment_service.TestcontainersPostgresConfiguration;
import com.erickWck.payment_service.configuration.AuditorJpa;
import com.erickWck.payment_service.domain.contractsignature.PixImplements;
import com.erickWck.payment_service.domain.repositories.PaymentRepository;
import com.erickWck.payment_service.domain.service.PixService;
import com.erickWck.payment_service.entity.Payment;
import com.erickWck.payment_service.entity.PaymentDtoTransaction;
import com.erickWck.payment_service.entity.PaymentStatus;
import com.erickWck.payment_service.entity.PaymentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@Import({TestcontainersPostgresConfiguration.class, AuditorJpa.class})
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class PixServiceIntegration {


    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private PixService pixService;

    @MockitoBean
    private PixImplements pixImplements;

    @BeforeEach
    void setup() {
        paymentRepository.deleteAll();
    }


    @Test
    void shouldProcessPixPayment_WhenTypeIsPix() {
        //arrange
        var inputPayment = createPayment();
        var outputPayment = createResponsePayment();
        when(pixImplements.processPayment(any(PaymentDtoTransaction.class)))
                .thenReturn(inputPayment);

        //act
        var result = pixService.verifyTypeIsPix(inputPayment);

        //assert
        assertNotNull(result);
        assertEquals(1, paymentRepository.count());
        verify(pixImplements, times(1)).processPayment(inputPayment);
        Optional<Payment> byBookId = paymentRepository.findByBookId(inputPayment.getBookId());
        assertTrue(byBookId.isPresent());
    }

    @Test
    void shouldReturnNull() {
        //arrange
        var inputPayment = createPayment();
        var outputPayment = createResponsePayment();
        inputPayment.setType("DEBIT_CARD");

        //act
        var result = pixService.verifyTypeIsPix(inputPayment);

        //assert
        assertNull(result);
        verify(pixImplements, never()).processPayment(any());
        assertEquals(0, paymentRepository.count());
    }

    @Test
    void shouldCreatePayment_WhenDoesNotExist() {
        //arrange
        var inputPayment = createPayment();
        var outputPayment = createResponsePayment();
        long id = inputPayment.getBookId();

        //act
        var result = pixService.createPayment(inputPayment);

        //assert
        assertNotNull(result);
        var findByBookId = paymentRepository.findByBookId(id);
        assertEquals(1, paymentRepository.count());
        assertTrue(findByBookId.isPresent());
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
