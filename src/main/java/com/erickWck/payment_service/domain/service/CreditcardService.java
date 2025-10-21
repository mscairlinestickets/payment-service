package com.erickWck.payment_service.domain.service;

import com.erickWck.payment_service.domain.contractsignature.CreditCardImplements;
import com.erickWck.payment_service.domain.mapper.PixDtoMapper;
import com.erickWck.payment_service.domain.repositories.PaymentRepository;
import com.erickWck.payment_service.entity.Payment;
import com.erickWck.payment_service.entity.PaymentDtoTransaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class CreditcardService {


    private final static Logger logger = LoggerFactory.getLogger(CreditcardService.class);

    private final CreditCardImplements creditCardImplements;
    private final PaymentRepository paymentRepository;


    public CreditcardService(CreditCardImplements cardImplements, PaymentRepository paymentRepository) {
        this.creditCardImplements = cardImplements;
        this.paymentRepository = paymentRepository;
    }

    public boolean verifyTypeIsCredidCard
            (PaymentDtoTransaction paymentDtoTransaction) {

        String type = paymentDtoTransaction.getType();
        if (type == null) {
            throw new IllegalArgumentException("O type não pode ser null.");
        }
        if (type.equals("CREDITO"))
            return true;
        else
            return false;
    }


    public Payment createPayment(PaymentDtoTransaction transaction) {

        var payment = PixDtoMapper.transactionToPaymentIfCredit(transaction);

        return paymentRepository.findByBookId(payment.getBookId())
                .orElseGet(() -> {
                    logger.info("Criando pagamento do tipo crédito. bookId={}, nome={}, valor={}",
                            payment.getBookId(), payment.getName(), payment.getAmount());
                    return paymentRepository.save(payment);
                });

    }

    public Payment payWithCreditcard(PaymentDtoTransaction transaction) {

        if (verifyTypeIsCredidCard(transaction)) {
            creditCardImplements.processPayment(transaction);
            return createPayment(transaction);
        }
        return null;
    }


}
