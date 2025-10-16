package com.erickWck.payment_service.domain.contractsignature;

import com.erickWck.payment_service.domain.contract.PixPayment;
import com.erickWck.payment_service.entity.PaymentDtoTransaction;
import com.erickWck.payment_service.entity.PaymentStatus;
import com.erickWck.payment_service.entity.PaymentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.time.LocalDateTime;

public class PixImplements implements PixPayment {

    private static final Logger log = LoggerFactory.getLogger(PixImplements.class);

    @Override
    public PaymentDtoTransaction payWithPix(PaymentDtoTransaction transaction) {

        if (transaction.getAmount() == null || transaction.getAmount().signum() <= 0) {
            throw new IllegalArgumentException("Valor de pagamento Pix deve ser maior que zero.");
        }

        // 2️ Define o tipo e o status inicial
        transaction.setPaymentType(PaymentType.PIX);
        transaction.setStatus(PaymentStatus.PENDING);

        log.info("Pix iniciado | Pedido {} | Cliente {} | Valor {} | Status {} | Chave Pix {}",
                transaction.getBookId(),
                transaction.getName(),
                transaction.getAmount(),
                transaction.getStatus(),
                transaction.getPixKey());

        // 3️ Simula o processamento e confirmação
        simulatePixConfirmation(transaction);
        return transaction;
    }

    private void simulatePixConfirmation(PaymentDtoTransaction transaction) {
        try {
            Thread.sleep(800); //
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        transaction.setStatus(PaymentStatus.APPROVED);
        log.info("Pix confirmado | Pedido {} | Cliente {} | Valor {} | Status {} | Chave Pix {} | Data {}",
                transaction.getBookId(),
                transaction.getName(),
                transaction.getAmount(),
                transaction.getStatus(),
                transaction.getPixKey(),
                LocalDateTime.now());
    }

}
