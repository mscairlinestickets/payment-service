package com.erickWck.payment_service.domain.service;

import com.erickWck.payment_service.domain.contractsignature.PixImplements;
import com.erickWck.payment_service.domain.mapper.PixDtoMapper;
import com.erickWck.payment_service.domain.repositories.PaymentRepository;
import com.erickWck.payment_service.entity.Payment;
import com.erickWck.payment_service.entity.PaymentDtoTransaction;
import org.springframework.stereotype.Service;

import static com.erickWck.payment_service.domain.mapper.PixDtoMapper.paymentDtoToPaymentIfxPix;

@Service
public class PixService {


    private final PixImplements pixImplements;
    private final PaymentRepository paymentRepository;

    public PixService(PixImplements pixImplements, PaymentRepository paymentRepository) {
        this.pixImplements = pixImplements;
        this.paymentRepository = paymentRepository;
    }


    public PaymentDtoTransaction verifyTypeIsPix(PaymentDtoTransaction paymentDtoTransaction) {
        if (paymentDtoTransaction.getType().equals("PIX")) {
            var payment = PixDtoMapper.paymentDtoToPaymentIfxPix(pixImplements.processPayment(paymentDtoTransaction));
            createPayment(paymentDtoTransaction);
            return paymentDtoTransaction;
        }
        return null;
    }

    public PaymentDtoTransaction payIfTypePix(PaymentDtoTransaction payment) {
        return pixImplements.processPayment(payment);
    }


    public Payment createPayment(PaymentDtoTransaction transaction) {

        var bookId = transaction.getBookId();
        var payment = paymentDtoToPaymentIfxPix(transaction);

        return paymentRepository.findByBookId(bookId)
                .orElseGet(() -> paymentRepository.save(payment));
    }

}
