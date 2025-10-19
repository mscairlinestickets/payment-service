package com.erickWck.payment_service.domain.contract;

import com.erickWck.payment_service.entity.PaymentDtoTransaction;

public interface Payment {

        PaymentDtoTransaction processPayment(PaymentDtoTransaction paymentDtoTransaction);

}
