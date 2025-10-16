package com.erickWck.payment_service.domain.contract;

import com.erickWck.payment_service.entity.PaymentDtoTransaction;

public interface CreditCardPayment extends Payment {

    void payWithCreditCard(PaymentDtoTransaction transaction);

}
