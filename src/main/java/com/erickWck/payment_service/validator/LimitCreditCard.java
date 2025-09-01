package com.erickWck.payment_service.validator;

import com.erickWck.payment_service.domain.CardTransaction;
import com.erickWck.payment_service.domain.PaymentStatus;
import com.erickWck.payment_service.domain.exception.LimitUnavailable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.smartcardio.Card;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class LimitCreditCard {


    private static final Logger log = LoggerFactory.getLogger(LimitCreditCard.class);


    public BigDecimal createLimitCard() {

        log.info("Criando um limite aleatorio para simular a validação.");
        double limit = Math.random() * 300000 + 1;
        return BigDecimal.valueOf(limit)
                .setScale(2, RoundingMode.HALF_UP);
    }

    public void limitPaymentCreditCard(CardTransaction card, double limitCard) {

        if (card.getAmount().doubleValue() > limitCard) {
            log.info("Pedido com ID: {} cliente: {} do cpf: {} Rejeitado.", card.getBookId(), card.getName(), card.getCardNumber());
            card.rejected();
            throw new LimitUnavailable(card);
        } else {
            log.info("Pedido com ID: {} cliente: {} do cpf: {} Aprovado.", card.getBookId(), card.getName(), card.getCpfNumber());
            card.approved();
        }

    }


}
