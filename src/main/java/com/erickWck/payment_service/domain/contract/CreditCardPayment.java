package com.erickWck.payment_service.domain.contract;

import com.erickWck.payment_service.entity.CardTransaction;

public interface CreditCardPayment extends Payment {

    void payWithCreditCard(CardTransaction transaction);

}
