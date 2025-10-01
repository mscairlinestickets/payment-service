package com.erickWck.payment_service.domain.service;

import com.erickWck.payment_service.domain.contract.Payment;
import com.erickWck.payment_service.domain.validator.LimitCreditCard;
import org.springframework.stereotype.Service;

public class PaymentService implements Payment {


    private LimitCreditCard limitCreditCard;

    public PaymentService(LimitCreditCard limitCreditCard) {
        this.limitCreditCard = limitCreditCard;
    }

}
