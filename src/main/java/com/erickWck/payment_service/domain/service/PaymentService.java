package com.erickWck.payment_service.domain.service;

import com.erickWck.payment_service.domain.contract.Payment;
import com.erickWck.payment_service.domain.contractsignature.CreditCardImplements;

public class PaymentService implements Payment {


    private CreditCardImplements limitCreditCard;

    public PaymentService(CreditCardImplements limitCreditCard) {
        this.limitCreditCard = limitCreditCard;
    }
}
