package com.erickWck.payment_service.domain.contract;

public interface BoletoPayment extends Payment {

    void payWithBoleto(Object object);
}
