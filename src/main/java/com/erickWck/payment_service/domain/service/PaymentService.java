package com.erickWck.payment_service.domain.service;

import com.erickWck.payment_service.domain.contract.Payment;
import com.erickWck.payment_service.entity.PaymentDtoTransaction;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;

@Service
public class PaymentService implements Payment {


    @KafkaListener(topics = "${app.consumetopic}", groupId = "${spring.application.name}")
    public void consume(@Header(KafkaHeaders.RECEIVED_KEY) Long key, PaymentDtoTransaction payload) {
        System.out.println("🔑 Key: " + key);
        System.out.println("📦 Mensagem recebida: " + payload);
    }

}
