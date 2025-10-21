package com.erickWck.payment_service.domain.service.boleto;

import com.erickWck.payment_service.domain.contractsignature.BoletoImplements;
import com.erickWck.payment_service.domain.repositories.PaymentRepository;
import com.erickWck.payment_service.domain.service.BoletoService;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BoletoServiceTest {

    @Mock
    private BoletoImplements boletoImplements;

    @Mock
    private PaymentRepository paymentRepository;

    @InjectMocks
    private BoletoService boletoService;

    @Nested
    class VerifyType {

        @Test
        void shouldVerifyTypeIsEqualsBoleto() {
            //arrrange
            var input = PaymentDtoTransaction.builder().type("BOLETO").build();

            //act
            var response = boletoService.verifyTypIsBoleto(input.getType());
            //assert
            assertNotNull(response);
            assertTrue(response);

        }

        @Test
        void shouldVerifyTypeIsNotEqualsBoleto() {
            //arrange
            var input = PaymentDtoTransaction.builder().type("PIX").build();
            //act
            var response = boletoService.verifyTypIsBoleto(input.getType());
            //asssert
            assertNotNull(response);
            assertFalse(response);
        }

        @Test
        void shouldVerifyTypeIsNullAndThrowException() {
            //arrange
            var input = new PaymentDtoTransaction();
            //act e asssert
            assertThrows(IllegalArgumentException.class, () -> boletoService.verifyTypIsBoleto(input.getType())).getMessage()
                    .equalsIgnoreCase("O type não pode ser null.");
        }
    }

    @Nested
    class SavePayment {

        @Test
        void shouldCreatePaymentWithSucess() {
            //arrange
            var inputPaymentDto = getPaymentDtoTransaction(2500.43);
            var outputPayment = getPayment();
            when(paymentRepository.findByBookId(anyLong())).thenReturn(Optional.empty());
            when(paymentRepository.save(any())).thenReturn(outputPayment);

            //act
            var response = boletoService.createPayment(inputPaymentDto);

            //assert
            assertNotNull(response);
            assertEquals(inputPaymentDto.getBookId(), response.getBookId());
            assertEquals(outputPayment.getStatus(), response.getStatus());
            assertEquals(outputPayment.getPaymentType(), response.getPaymentType());
            verify(paymentRepository, times(1)).save(any());
            verify(paymentRepository, times(1)).findByBookId(anyLong());
        }

        @Test
        void shouldReturnExistingPayment() {
            //arrange
            var inputPaymentDto = getPaymentDtoTransaction(2500.43);
            var outputpayment = getPayment();
            when(paymentRepository.findByBookId(anyLong())).thenReturn(Optional.of(outputpayment));

            //act
            var response = boletoService.createPayment(inputPaymentDto);

            //assert
            assertNotNull(response);
            assertEquals(outputpayment.getBookId(), response.getBookId());
            assertEquals(outputpayment.getStatus(), response.getStatus());
            assertEquals(outputpayment.getAmount(), response.getAmount());
            verify(paymentRepository, times(1)).findByBookId(anyLong());
            verify(paymentRepository, never()).save(any());
        }
    }

    @Nested
    class PayWithBoleto {

        @Test
        void shouldPayWithBoletoWithSuccess() {
            //arrange
            var inputPaymentDto = getPaymentDtoTransaction(2500.43);
            var outputpayment = getPayment();
            when(paymentRepository.findByBookId(anyLong())).thenReturn(Optional.empty());
            when(paymentRepository.save(any())).thenReturn(outputpayment);
            //act
            var response = boletoService.payWithBoleto(inputPaymentDto);
            //assert
            assertEquals(outputpayment.getBookId(), response.getBookId());
            assertNotEquals(inputPaymentDto.getStatus(), response.getStatus());
            verify(paymentRepository, times(1)).save(any());
            verify(paymentRepository, times(1)).findByBookId(anyLong());
        }

        @Test
        void shouldReturnPayWhenAlreadyIfExistingPayment() {
            //arrange
            var inputPaymentDto = getPaymentDtoTransaction(2500.43);
            var outputpayment = getPayment();
            when(paymentRepository.findByBookId(anyLong())).thenReturn(Optional.of(outputpayment));

            //act
            var response = boletoService.payWithBoleto(inputPaymentDto);

            //assert
            assertEquals(outputpayment.getBookId(), response.getBookId());
            assertEquals(outputpayment.getStatus(), response.getStatus());
            assertEquals(outputpayment.getId(), response.getId());
            verify(paymentRepository, times(1)).findByBookId(anyLong());
            verify(paymentRepository, never()).save(any());
        }

        @Test
        void shouldReturnWhenTypeIsNotBoleto() {
            //arrange
            var inputPaymentDto = getPaymentDtoTransaction(2500.43);
            inputPaymentDto.setType("PIX");

            //act
            var response = boletoService.payWithBoleto(inputPaymentDto);

            //arrange
            assertNull(response);
            verifyNoInteractions(paymentRepository);
            verifyNoInteractions(boletoImplements);
        }

        @Test
        void shouldThrowExceptionWhenTypeIsNull() {
            //arrange
            var inputDtoPayment = PaymentDtoTransaction.builder().type(null).build();

            //act e assert
            assertThrows(IllegalArgumentException.class, () -> boletoService.payWithBoleto(inputDtoPayment));
        }
    }

    private static PaymentDtoTransaction getPaymentDtoTransaction(double amount) {
        return PaymentDtoTransaction.builder()
                .bookId(132L)
                .name("Erick Nunes")
                .cpfNumber("123.456.789-00")
                .amount(new BigDecimal("1508.00"))
                .type("BOLETO")
                .status(PaymentStatus.PENDING)
                .availableAmount(BigDecimal.valueOf(amount))
                .build();
    }

    private static Payment getPayment() {
        return Payment.builder()
                .bookId(132L)
                .name("Erick Nunes")
                .cpfNumber("123.456.789-00")
                .status(PaymentStatus.APPROVED)
                .paymentType(PaymentType.BOLETO)
                .amount(new BigDecimal("1508.00"))
                .type("BOLETO")
                .build();
    }

}
