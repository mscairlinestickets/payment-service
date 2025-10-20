package com.erickWck.payment_service.domain.service.credit;

import com.erickWck.payment_service.domain.contractsignature.CreditCardImplements;
import com.erickWck.payment_service.domain.repositories.PaymentRepository;
import com.erickWck.payment_service.domain.service.CreditcardService;
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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class CreditcardServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private CreditCardImplements creditCardImplements;

    @InjectMocks
    private CreditcardService creditcardService;

    @Nested
    class VerifyTyoeCredit {

        @Test
        void shouldVerifyTypeIsCredidCard() {
            //arrange
            String type = "CREDITO";
            var paymentDto = PaymentDtoTransaction
                    .builder().type(type).build();
            //act
            var response = creditcardService.verifyTypeIsCredidCard(paymentDto);

            //assert
            assertTrue(response);
        }

        @Test
        void shouldNotVerifyTypeIsCredidCard() {
            //arrange
            String type = "PIX";
            var paymentDto = PaymentDtoTransaction
                    .builder().type(type).build();
            //act
            var response = creditcardService.verifyTypeIsCredidCard(paymentDto);

            //assert
            assertFalse(response);
        }

        @Test
        void shouldThrowIllegalArgumentException() {
            //arrange
            var paymentDto = PaymentDtoTransaction
                    .builder().type(null).build();
            //act e assert
            assertThrows(IllegalArgumentException.class, () -> creditcardService.verifyTypeIsCredidCard(paymentDto))
                    .getMessage().equalsIgnoreCase("O type não pode ser null.");

        }
    }

    @Nested
    class CreatePayment {

        @Test
        void shouldCreatePaymentWithSuccess() {
            //arrange
            var inputPaymentDto = geetPaymentDtoTransaction(2500.43);
            var outputPayment = geetPayment();
            when(paymentRepository.findByBookId(anyLong())).thenReturn(Optional.empty());
            when(paymentRepository.save(any())).thenReturn(outputPayment);
            //act

            var response = creditcardService.createPayment(inputPaymentDto);

            //assert
            assertNotNull(response);
            assertEquals(inputPaymentDto.getBookId(), response.getBookId());
            verify(paymentRepository, times(1)).save(any());


        }


        @Test
        void shouldReturnExistingPayment() {
            //arrange
            var inputPaymentDto = geetPaymentDtoTransaction(2500.43);
            var outputPayment = geetPayment();
            when(paymentRepository.findByBookId(anyLong())).thenReturn(Optional.of(outputPayment));

            //act
            var response = creditcardService.createPayment(inputPaymentDto);

            //assert
            assertNotNull(response);
            assertEquals(outputPayment.getBookId(), response.getBookId());
            verify(paymentRepository, never()).save(any());

        }

    }

    @Nested
    class PayWithcreditcard {

        @Test
        void shouldPayWithCreditCardWithSuccess() {
            //arrange
            var inputPaymentDto = geetPaymentDtoTransaction(2500.43);
            var outputPayment = geetPayment();
            when(paymentRepository.findByBookId(any())).thenReturn(Optional.empty());
            when(paymentRepository.save(any())).thenReturn(outputPayment);

            //act

            var response = creditcardService.payWithCreditcard(inputPaymentDto);

            //assert
            assertNotNull(response);
            assertEquals(outputPayment.getBookId(), response.getBookId());
            assertEquals(PaymentStatus.APPROVED, response.getStatus());
            verify(paymentRepository, times(1)).save(any());
            verify(creditCardImplements, times(1)).processPayment(any());
        }

        @Test
        void shouldNotPayWhenDoesNotTypeIsCreditCard() {
            //arrange
            var inputPaymentDto = geetPaymentDtoTransaction(2500.43);
            inputPaymentDto.setType("pix");
            var outputPayment = geetPayment();

            //act
            var response = creditcardService.payWithCreditcard(inputPaymentDto);
            //assert
            assertNull(response);
            verifyNoInteractions(paymentRepository);
            verifyNoInteractions(creditCardImplements);
        }


        @Test
        void shouldReturnWhenAlreadyIfExistingPayment() {
            //arrange
            var inputPaymentDto = geetPaymentDtoTransaction(2500.43);
            var outputPayment = geetPayment();
            when(paymentRepository.findByBookId(anyLong())).thenReturn(Optional.of(outputPayment));

            //act
            var response = creditcardService.payWithCreditcard(inputPaymentDto);

            //assert
            assertNotNull(response);
            assertEquals(outputPayment.getBookId(), response.getBookId());
            assertEquals(PaymentStatus.APPROVED, response.getStatus());
            verify(paymentRepository, never()).save(any());
            verify(creditCardImplements, times(1)).processPayment(any());
        }

        @Test
        void shouldThrowExceptionWhenTypeIsNull() {
            //arrange
            var inputPaymentDto = geetPaymentDtoTransaction(2500.43);
            inputPaymentDto.setType(null);
            //act e assert

            assertThrows(IllegalArgumentException.class, () -> creditcardService.payWithCreditcard(inputPaymentDto))
                    .getMessage().equalsIgnoreCase("O type não pode ser null.");
        }

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
