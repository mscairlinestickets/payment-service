package com.erickWck.payment_service.exception;

import com.erickWck.payment_service.entity.CardTransaction;

public class LimitUnavailable extends RuntimeException {

    public LimitUnavailable(CardTransaction card){
        super("Pedido com ID: " + card.getBookId() + "rejeitado, limite insuficiente.");
    }

}
