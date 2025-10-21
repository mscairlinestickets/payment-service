package com.erickWck.payment_service.domain.mapper;

import com.erickWck.payment_service.entity.Payment;
import com.erickWck.payment_service.entity.PaymentDtoTransaction;
import com.erickWck.payment_service.entity.PaymentStatus;
import com.erickWck.payment_service.entity.PaymentType;
import lombok.experimental.UtilityClass;

@UtilityClass
public class PixDtoMapper {

    public static Payment transactionDtoToPaymentIfxPix(PaymentDtoTransaction transaction) {
        return Payment.builder()
                .bookId(transaction.getBookId())
                .name(transaction.getName())
                .cpfNumber(transaction.getCpfNumber())
                .pixKey(transaction.getPixKey())
                .amount(transaction.getAmount())
                .type(transaction.getType())
                .status(PaymentStatus.APPROVED)
                .paymentType(PaymentType.PIX)
                .createdAt(null)
                .lastModifiedAt(null)
                .version(0)
                .build();
    }


    public static Payment transactionToPaymentIfCredit(PaymentDtoTransaction paymentDtoTransaction) {
        return Payment.builder()
                .bookId(paymentDtoTransaction.getBookId())
                .name(paymentDtoTransaction.getName())
                .cpfNumber(paymentDtoTransaction.getCpfNumber())
                .cardholderName(paymentDtoTransaction.getCardholderName())
                .cardNumber(paymentDtoTransaction.getCardNumber())
                .expiryDate(paymentDtoTransaction.getExpiryDate())
                .cvv(paymentDtoTransaction.getCvv())
                .amount(paymentDtoTransaction.getAmount())
                .type(paymentDtoTransaction.getType())
                .status(PaymentStatus.APPROVED)
                .paymentType(PaymentType.CREDIT_CARD)
                .createdAt(null)
                .lastModifiedAt(null)
                .version(0)
                .build();
    }

    public  static Payment transactionToPaymentIfBoleto(PaymentDtoTransaction transaction){
        return Payment.builder()
                .bookId(transaction.getBookId())
                .amount(transaction.getAmount())
                .name(transaction.getName())
                .cpfNumber(transaction.getCpfNumber())
                .type(transaction.getType())
                .status(PaymentStatus.APPROVED)
                .lastModifiedAt(null)
                .createdAt(null)
                .version(0)
                .build();
    }


}
