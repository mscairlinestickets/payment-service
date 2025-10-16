package com.erickWck.payment_service.domain.contract;

import com.erickWck.payment_service.entity.PaymentDtoTransaction;

public interface BoletoPayment extends Payment {

    PaymentDtoTransaction payWithBoleto(PaymentDtoTransaction transaction);

}
