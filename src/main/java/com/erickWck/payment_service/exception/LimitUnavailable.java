package com.erickWck.payment_service.exception;

import com.erickWck.payment_service.entity.PaymentDtoTransaction;

public class LimitUnavailable extends RuntimeException {

    public LimitUnavailable(PaymentDtoTransaction card){
        super("Pedido com ID: " + card.getBookId() + "rejeitado, limite insuficiente.");
    }

}
