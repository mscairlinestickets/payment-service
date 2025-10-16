package com.erickWck.payment_service.domain.contractsignature;

import com.erickWck.payment_service.domain.contract.BoletoPayment;
import com.erickWck.payment_service.entity.PaymentDtoTransaction;
import com.erickWck.payment_service.entity.PaymentStatus;
import com.erickWck.payment_service.entity.PaymentType;
import com.erickWck.payment_service.exception.LimitUnavailable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class BoletoImplements implements BoletoPayment {

    Logger log = LoggerFactory.getLogger(BoletoImplements.class);

    @Override
    public PaymentDtoTransaction payWithBoleto(PaymentDtoTransaction payment) {

        payment.setPaymentType(PaymentType.BOLETO);

        validLimitIsValid(payment);

        // Simula boleto gerado
        String boletoFake = generateBoletoCode();
        payment.setStatus(PaymentStatus.PENDING);

        log.info("Boleto gerado | Pedido {} | Cliente {} | Valor {} | Status {} | Linha Digitável {}",
                payment.getBookId(), payment.getName(), payment.getAmount(), payment.getStatus(), boletoFake);

        simulateBoletoConfirmation(payment);
        return payment;
    }

    private void validLimitIsValid(PaymentDtoTransaction payment) {
        var limit = payment.getAvailableAmount();
        if (limit.compareTo(BigDecimal.ZERO) <= 0 || payment.getAmount().compareTo(limit) > 0) {
            payment.setStatus(PaymentStatus.REJECTED);
            log.info("Pedido {} | Cliente {} | CPF {} | Valor {} | Limite {} | REJEITADO - Limite insuficiente.", payment.getBookId(), payment.getName(), payment.getAmount(), payment.getStatus());
            throw new LimitUnavailable(payment);
        }
    }

    private String generateBoletoCode() {
        return "34191." + UUID.randomUUID().toString().replace("-", "").substring(0, 25);
    }

    private void simulateBoletoConfirmation(PaymentDtoTransaction transaction) {
        try {
            Thread.sleep(800); //
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        transaction.setStatus(PaymentStatus.APPROVED);
        log.info("Boleto confirmado | Pedido {} | Cliente {} | Valor {} | Status {} | Data {}",
                transaction.getBookId(),
                transaction.getName(),
                transaction.getAmount(),
                transaction.getStatus(),
                LocalDateTime.now());
    }
}
