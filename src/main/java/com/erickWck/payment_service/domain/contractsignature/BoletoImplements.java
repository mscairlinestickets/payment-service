package com.erickWck.payment_service.domain.signature;

import com.erickWck.payment_service.domain.contract.BoletoPayment;
import com.erickWck.payment_service.entity.CardTransaction;
import com.erickWck.payment_service.entity.PaymentStatus;
import com.erickWck.payment_service.entity.PaymentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

public class BoletoImplements implements BoletoPayment {

    Logger log = LoggerFactory.getLogger(BoletoImplements.class);

    @Override
    public void payWithBoleto(Object object) {

        if (!(object instanceof CardTransaction card)) {
            throw new IllegalArgumentException("Objeto inválido para pagamento com boleto.");
        }

        card.setPaymentType(PaymentType.BOLETO);

        // Simula boleto gerado
        card.setStatus(PaymentStatus.PENDING);
        String boletoFake = generateBoletoCode();

        log.info("Boleto gerado | Pedido {} | Cliente {} | Valor {} | Status {} | Linha Digitável {}",
                card.getBookId(), card.getName(), card.getAmount(), card.getStatus(), boletoFake);
    }

    private String generateBoletoCode() {
        return "34191." + UUID.randomUUID().toString().replace("-", "").substring(0, 25);
    }

}
