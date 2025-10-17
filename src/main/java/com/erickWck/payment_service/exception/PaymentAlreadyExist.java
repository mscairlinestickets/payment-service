package com.erickWck.payment_service.exception;

public class PaymentAlreadyExist extends RuntimeException{

    public PaymentAlreadyExist(Long bookId){
        super("Pagamento com bookId: " + bookId + " já existe.");
    }

}
