package com.erickWck.payment_service.domain.service;

import com.erickWck.payment_service.domain.contractsignature.PixImplements;
import com.erickWck.payment_service.domain.mapper.PixDtoMapper;
import com.erickWck.payment_service.domain.repositories.PaymentRepository;
import com.erickWck.payment_service.entity.Payment;
import com.erickWck.payment_service.entity.PaymentDtoTransaction;
import com.erickWck.payment_service.exception.PaymentAlreadyExist;
import org.springframework.stereotype.Service;

import static com.erickWck.payment_service.domain.mapper.PixDtoMapper.paymentDtoToPayment;

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
            return pixImplements.payWithPix(paymentDtoTransaction);
        }
        return null;
    }


    public PaymentDtoTransaction payIfTypePix(PaymentDtoTransaction payment) {
        return pixImplements.payWithPix(payment);
    }


    public Payment createPayment(PaymentDtoTransaction transaction) {

        var bookId = transaction.getBookId();
        var payment = paymentDtoToPayment(transaction);

        return paymentRepository.findByBookId(bookId)
                .orElseGet(() -> paymentRepository.save(payment));
    }
}
