package com.erickWck.payment_service.domain.mapper;

import com.erickWck.payment_service.entity.Payment;
import com.erickWck.payment_service.entity.PaymentDtoTransaction;
import com.erickWck.payment_service.entity.PaymentStatus;
import lombok.experimental.UtilityClass;

@UtilityClass
public class PixDtoMapper {

    public static Payment paymentDtoToPayment(PaymentDtoTransaction transaction) {
        return  Payment.builder()
                .bookId(transaction.getBookId())
                .name(transaction.getName())
                .cpfNumber(transaction.getCpfNumber())
                .pixKey(transaction.getPixKey())
                .amount(transaction.getAmount())
                .type(transaction.getType())
                .status(PaymentStatus.APPROVED)
                .paymentType(transaction.getPaymentType())
                .createdAt(null)
                .lastModifiedAt(null)
                .version(0)
                .build();
    }


}
