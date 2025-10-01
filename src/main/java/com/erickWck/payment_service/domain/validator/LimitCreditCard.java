package com.erickWck.payment_service.domain.validator;

import com.erickWck.payment_service.domain.contract.CreditCardPayment;
import com.erickWck.payment_service.entity.CardTransaction;
import com.erickWck.payment_service.exception.LimitUnavailable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class LimitCreditCard implements CreditCardPayment {

    private static final Logger log = LoggerFactory.getLogger(LimitCreditCard.class);

    @Override
    public void payWithCreditCard(CardTransaction card) {

        BigDecimal limit = card.getAvailableAmount();
        if (limit.compareTo(BigDecimal.ZERO) <= 0 || card.getAmount().compareTo(limit) > 0) {
            log.info("Pedido {} | Cliente {} | CPF {} | Valor {} | Limite {} | REJEITADO",
                    card.getBookId(), card.getName(), card.getCardNumber(),
                    card.getAmount(), card.getAvailableAmount());
            card.rejected();
            throw new LimitUnavailable(card);
        } else {
            log.info("Pedido {} | Cliente {} | CPF {} | Valor {} | Limite {} | APROVADO",
                    card.getBookId(), card.getName(), card.getCpfNumber(),
                    card.getAmount(), card.getAvailableAmount());
            card.approved();
        }
    }

    public BigDecimal createLimitCard() {

        log.info("Criando um limite aleatorio para simular a validação.");
        double limit = Math.random() * 300000 + 1;
        return BigDecimal.valueOf(limit)
                .setScale(2, RoundingMode.HALF_UP);
    }
}
