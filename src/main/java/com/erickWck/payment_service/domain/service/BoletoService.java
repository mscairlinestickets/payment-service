package com.erickWck.payment_service.domain.service;

import com.erickWck.payment_service.domain.contractsignature.BoletoImplements;
import com.erickWck.payment_service.domain.mapper.PixDtoMapper;
import com.erickWck.payment_service.domain.repositories.PaymentRepository;
import com.erickWck.payment_service.entity.Payment;
import com.erickWck.payment_service.entity.PaymentDtoTransaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class BoletoService {

    private final static Logger logger = LoggerFactory.getLogger(BoletoService.class);

    private final BoletoImplements boletoImplements;
    private final PaymentRepository paymentRepository;

    public BoletoService(BoletoImplements boletoImplements, PaymentRepository paymentRepository) {
        this.boletoImplements = boletoImplements;
        this.paymentRepository = paymentRepository;
    }

    public boolean verifyTypIsBoleto(String type) {

        if (type == null) {
            throw new IllegalArgumentException("O type não pode ser null.");
        }

        if (type.equals("BOLETO")) {
            return true;
        } else {
            return false;
        }
    }


    public Payment createPayment(PaymentDtoTransaction paymentDtoTransaction) {
        var payment = PixDtoMapper.transactionToPaymentIfBoleto(paymentDtoTransaction);
        return paymentRepository.findByBookId(paymentDtoTransaction.getBookId())
                .orElseGet(() -> {
                    logger.info("Criando pagamento do tipo Boleto. bookId={}, nome={}, valor={}, code={}",
                            payment.getBookId(), payment.getName(), payment.getAmount(), payment.getCodeBoleto());
                    return paymentRepository.save(payment);
                });
    }

    public Payment payWithBoleto(PaymentDtoTransaction paymentDtoTransaction) {

        if (verifyTypIsBoleto(paymentDtoTransaction.getType())) {
            boletoImplements.processPayment(paymentDtoTransaction);
            return createPayment(paymentDtoTransaction);
        }
        return null;
    }

}
