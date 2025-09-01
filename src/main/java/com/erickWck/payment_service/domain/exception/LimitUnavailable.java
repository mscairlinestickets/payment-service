package com.erickWck.payment_service.domain.exception;

import com.erickWck.payment_service.domain.CardTransaction;

public class LimitUnavailable extends RuntimeException {

    public LimitUnavailable(CardTransaction card){
        super("Pedido com ID: " + card.getBookId() + "rejeitado, limite insuficiente.");
    }

}
