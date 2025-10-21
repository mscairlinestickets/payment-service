package com.erickWck.payment_service.domain.service.boleto;

import com.erickWck.payment_service.TestcontainersPostgresConfiguration;
import com.erickWck.payment_service.configuration.AuditorJpa;
import com.erickWck.payment_service.domain.contractsignature.BoletoImplements;
import com.erickWck.payment_service.domain.repositories.PaymentRepository;
import com.erickWck.payment_service.domain.service.BoletoService;
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
public class BoletoIntegrationTest {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private BoletoService boletoService;

    @MockitoBean
    private BoletoImplements boletoImplements;

    private final Long BOOK_ID = 132L;

    @BeforeEach
    void setup() {
        paymentRepository.deleteAll();
        reset(boletoImplements);
    }


    @Nested
    class CreatePayment {

        @Test
        void shouldCreatePaymentWithSucessAndPersistToDB() {
            // Arrange
            var inputPaymentDto = getPaymentDtoTransaction(PaymentType.BOLETO.name(), new BigDecimal("2500.00"));

            // Ação
            var response = boletoService.createPayment(inputPaymentDto);

            // Assert
            assertNotNull(response);
            Optional<Payment> persisted = paymentRepository.findByBookId(BOOK_ID);
            assertTrue(persisted.isPresent());
            assertEquals(BOOK_ID, persisted.get().getBookId());
            assertEquals(PaymentStatus.APPROVED, persisted.get().getStatus()); // Status setado no Mapper/Service
            assertEquals(1, paymentRepository.count());
        }

        @Test
        void shouldReturnExistingPaymentFromDB() {
            // Arrange
            Payment existingPayment = getPayment(BOOK_ID, PaymentStatus.APPROVED);
            paymentRepository.save(existingPayment);

            var inputPaymentDto = getPaymentDtoTransaction(PaymentType.BOLETO.name(), new BigDecimal("2500.00"));

            // Act
            var response = boletoService.createPayment(inputPaymentDto);

            // Assert
            assertNotNull(response);
            assertEquals(existingPayment.getId(), response.getId());
            assertEquals(existingPayment.getStatus(), response.getStatus());
            assertEquals(1, paymentRepository.count());
        }
    }

    @Nested
    class PayWithBoleto {

        @Test
        void shouldPayWithBoletoWithSuccessAndCreateNewPayment() {
            //arrange
            var inputPaymentDto = getPaymentDtoTransaction(PaymentType.BOLETO.name(), new BigDecimal("2500.00"));
            when(boletoImplements.processPayment(any(PaymentDtoTransaction.class)))
                    .thenReturn(inputPaymentDto);

            // Act
            var response = boletoService.payWithBoleto(inputPaymentDto);

            // Assert
            assertNotNull(response);
            assertEquals(BOOK_ID, response.getBookId());
            assertEquals(PaymentStatus.APPROVED, response.getStatus());
            verify(boletoImplements, times(1)).processPayment(inputPaymentDto);
            assertEquals(1, paymentRepository.count());
        }

        @Test
        void shouldReturnExistingPaymentWhenAlreadyPaid() {
            // Arrange:
            Payment existingPayment = getPayment(BOOK_ID, PaymentStatus.APPROVED);
            paymentRepository.save(existingPayment);

            var inputPaymentDto = getPaymentDtoTransaction(PaymentType.BOLETO.name(), new BigDecimal("2500.00"));
            when(boletoImplements.processPayment(any(PaymentDtoTransaction.class)))
                    .thenReturn(inputPaymentDto);

            // Act
            var response = boletoService.payWithBoleto(inputPaymentDto);

            // Assert
            assertNotNull(response);
            assertEquals(existingPayment.getId(), response.getId());

            verify(boletoImplements, times(1)).processPayment(inputPaymentDto);
            assertEquals(1, paymentRepository.count());
        }

        @Test
        void shouldReturnNullWhenTypeIsNotBoleto() {
            var inputPaymentDto = getPaymentDtoTransaction(PaymentType.PIX.name(), new BigDecimal("2500.00"));

            // Act
            var response = boletoService.payWithBoleto(inputPaymentDto);

            // Assert
            assertNull(response);

            verifyNoInteractions(boletoImplements);
            assertEquals(0, paymentRepository.count());
        }

        @Test
        void shouldThrowExceptionWhenTypeIsNull() {
            // Act e Assert
            assertThrows(IllegalArgumentException.class, () -> boletoService.payWithBoleto(
                    getPaymentDtoTransaction(null, new BigDecimal("2500.00"))
            ));

            verifyNoInteractions(boletoImplements);
        }
    }

    private static PaymentDtoTransaction getPaymentDtoTransaction(String type, BigDecimal availableLimit) {
        return PaymentDtoTransaction.builder()
                .bookId(132L)
                .name("Erick Nunes")
                .cpfNumber("123.456.789-00")
                .amount(new BigDecimal("1508.00"))
                .type(type)
                .status(PaymentStatus.PENDING) // Status inicial
                // Garante que o limite é maior que o valor para não lançar LimitUnavailable
                .availableAmount(availableLimit)
                .build();
    }

    private static Payment getPayment(Long bookId, PaymentStatus status) {
        return Payment.builder()
                .bookId(bookId)
                .name("Erick Nunes")
                .cpfNumber("123.456.789-00")
                .status(status)
                .paymentType(PaymentType.BOLETO)
                .amount(new BigDecimal("1508.00"))
                .codeBoleto("FAKE_BOLETO_CODE")
                .type("BOLETO")
                .build();
    }
}