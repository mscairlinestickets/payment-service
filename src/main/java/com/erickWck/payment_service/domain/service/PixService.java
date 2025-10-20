package com.erickWck.payment_service.domain.service;

import com.erickWck.payment_service.domain.contractsignature.PixImplements;
import com.erickWck.payment_service.domain.mapper.PixDtoMapper;
import com.erickWck.payment_service.domain.repositories.PaymentRepository;
import com.erickWck.payment_service.entity.Payment;
import com.erickWck.payment_service.entity.PaymentDtoTransaction;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.erickWck.payment_service.domain.mapper.PixDtoMapper.transactionDtoToPaymentIfxPix;

@Service
public class PixService {

    private static final Logger log = LoggerFactory.getLogger(PixService.class);

    private final PixImplements pixImplements;
    private final PaymentRepository paymentRepository;

    public PixService(PixImplements pixImplements, PaymentRepository paymentRepository) {
        this.pixImplements = pixImplements;
        this.paymentRepository = paymentRepository;
    }

    public PaymentDtoTransaction verifyTypeIsPix(PaymentDtoTransaction paymentDtoTransaction) {
        log.info("Iniciando verificação de tipo de pagamento para BookId: {}", paymentDtoTransaction.getBookId());

        if (paymentDtoTransaction.getType().equals("PIX")) {
            log.debug("Tipo 'PIX' identificado. Processando pagamento com PixImplements.");

            var processedPaymentDto = pixImplements.processPayment(paymentDtoTransaction);
            var paymentEntity = PixDtoMapper.transactionDtoToPaymentIfxPix(processedPaymentDto);

            log.info("Pagamento PIX processado. Status retornado: {}. Tentando criar registro.", processedPaymentDto.getStatus());

            createPayment(paymentDtoTransaction);

            log.info("Registro de pagamento criado/atualizado com sucesso para BookId: {}", paymentDtoTransaction.getBookId());

            return paymentDtoTransaction;
        }

        log.info("Tipo de pagamento '{}' não é PIX. Encerrando o processamento para BookId: {}",
                paymentDtoTransaction.getType(), paymentDtoTransaction.getBookId());
        return null;
    }


    public Payment createPayment(PaymentDtoTransaction transaction) {

        var bookId = transaction.getBookId();
        var payment = transactionDtoToPaymentIfxPix(transaction);

        log.debug("Verificando se pagamento para BookId {} já existe.", bookId);

        return paymentRepository.findByBookId(bookId)
                .orElseGet(() -> {
                    log.info("Pagamento para BookId {} não encontrado. Salvando novo registro com status: {}",
                            bookId, payment.getStatus());
                    return paymentRepository.save(payment);
                });
    }

}