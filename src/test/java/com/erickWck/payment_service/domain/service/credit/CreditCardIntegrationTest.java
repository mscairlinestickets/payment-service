package com.erickWck.payment_service.domain.service.credit;

import com.erickWck.payment_service.TestcontainersPostgresConfiguration;
import com.erickWck.payment_service.configuration.AuditorJpa;
import com.erickWck.payment_service.domain.contractsignature.CreditCardImplements;
import com.erickWck.payment_service.domain.repositories.PaymentRepository;
import com.erickWck.payment_service.domain.service.CreditcardService;
import com.erickWck.payment_service.entity.Payment;
import com.erickWck.payment_service.entity.PaymentDtoTransaction;
import com.erickWck.payment_service.entity.PaymentStatus;
import com.erickWck.payment_service.entity.PaymentType;
import com.erickWck.payment_service.exception.LimitUnavailable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@Import({TestcontainersPostgresConfiguration.class, AuditorJpa.class})
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CreditCardIntegrationTest {

    @Autowired
    private PaymentRepository paymentRepository;

    @MockitoBean
    private CreditCardImplements creditCardImplements;

    @Autowired
    private CreditcardService creditcardService;

    @BeforeEach
    void setup() {
        paymentRepository.deleteAll();
    }

    @Test
    void shouldCreatePaymentWithSucess() {
        //arrange
        var inputPayment = geetPaymentDtoTransaction(2500.65);
        var outputPayment = geetPayment();
        long id = inputPayment.getBookId();
        ArgumentCaptor<Payment> captor = ArgumentCaptor.forClass(Payment.class);

        //act
        var response = creditcardService.payWithCreditcard(inputPayment);

        //assert
        assertNotNull(response);
        assertEquals(outputPayment.getBookId(), response.getBookId());
        assertNotNull(response.getCreatedAt());
        verify(creditCardImplements, times(1)).processPayment(inputPayment);
        var findPayment = paymentRepository.findByBookId(id);
        assertEquals(1, paymentRepository.count());
        assertTrue(findPayment.isPresent());
    }

    @Test
    void shouldNotCreatePaymentWhenTypeIsNotCreditCard() {
        //arrange
        var inputPayment = geetPaymentDtoTransaction(500.00);
        inputPayment.setType("PIX");

        //act
        var response = creditcardService.payWithCreditcard(inputPayment);

        //assert
        assertNull(response);
        assertEquals(0, paymentRepository.count());
        verify(creditCardImplements, times(0)).processPayment(inputPayment);
    }

    @Test
    void shouldNotCreatePaymentWhenTypeIsNull() {
        //arrange
        var inputPayment = geetPaymentDtoTransaction(500.00);
        inputPayment.setType(null);

        //act
        assertThrows(IllegalArgumentException.class, () -> creditcardService.payWithCreditcard(inputPayment));

        //assert
        assertEquals(0, paymentRepository.count());
        verify(creditCardImplements, times(0)).processPayment(inputPayment);
    }

    @Test
    void shouldNotCreateDuplicatePaymentWhenBookIdAlreadyExists() {
        //arrange
        var inputPayment = geetPaymentDtoTransaction(2500.65);
        var outputPayment = geetPayment();
        var duplicate = geetPaymentDtoTransaction(2600.65);

        creditcardService.payWithCreditcard(inputPayment);

        //act
        var response = creditcardService.payWithCreditcard(duplicate);

        //assert
        assertNotNull(response);
        assertEquals(1, paymentRepository.count());
        verify(creditCardImplements, times(1)).processPayment(inputPayment);
    }

    @Test
    void shouldThrowExceptionWhenLimitExceeded() {
        //arrange
        var creditCardImplements = spy(CreditCardImplements.class);
        var inputPayment = geetPaymentDtoTransaction(250.65);
        when(creditCardImplements.createLimitCard()).thenReturn(BigDecimal.valueOf(250));

        //act e assert
        assertThrows(LimitUnavailable.class, () -> creditCardImplements.processPayment(inputPayment))
                .getMessage().equalsIgnoreCase("Pedido com ID: " + inputPayment.getBookId() + "rejeitado, limite insuficiente.");
        assertEquals(0, paymentRepository.count());

    }

    private static PaymentDtoTransaction geetPaymentDtoTransaction(double valor) {
        return PaymentDtoTransaction.builder()
                .bookId(132L)
                .name("Erick Nunes")
                .cpfNumber("123.456.789-00")
                .cardholderName("Erick Nunes")
                .cardNumber("4111111111111111")
                .expiryDate("12/26")
                .cvv("123")
                .amount(new BigDecimal("1508.00"))
                .type("CREDITO")
                .status(PaymentStatus.PENDING)
                .availableAmount(BigDecimal.valueOf(valor))
                .build();
    }

    private static Payment geetPayment() {
        return Payment.builder()
                .bookId(132L)
                .name("Erick Nunes")
                .cpfNumber("123.456.789-00")
                .cardholderName("Erick Nunes")
                .cardNumber("4111111111111111")
                .expiryDate("12/26")
                .cvv("123")
                .status(PaymentStatus.APPROVED)
                .paymentType(PaymentType.CREDIT_CARD)
                .amount(new BigDecimal("1508.00"))
                .type("CREDITO")
                .build();
    }

}
