package com.erickWck.payment_service.events;


import com.erickWck.payment_service.entity.PaymentDtoTransaction;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.LongDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.*;

@EnableKafka
@Configuration
public class KafkaConfig {

    @Value(value = "${app.localhost}")
    private String localHost;

    @Value(value = "${app.typeoffseat}")
    private String typeoffseat;

    @Value(value = "${spring.application.name}")
    private String groupId;

    @Bean
    public ConsumerFactory<Long, PaymentDtoTransaction> getProperties() {
        Map<String, Object> properties = new HashMap<>();

        JsonDeserializer<PaymentDtoTransaction> jsonDeserializer = new JsonDeserializer<>(PaymentDtoTransaction.class);
        jsonDeserializer.addTrustedPackages("*");
        jsonDeserializer.ignoreTypeHeaders();

        properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, localHost);
        properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, LongDeserializer.class.getName());
        properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class.getName());
        properties.put(ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS, JsonDeserializer.class);
        properties.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        properties.put(ConsumerConfig.CLIENT_ID_CONFIG, UUID.randomUUID().toString());
        properties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, typeoffseat);

        return new DefaultKafkaConsumerFactory<>(properties, new LongDeserializer(), jsonDeserializer);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<Long, PaymentDtoTransaction>
    kafkaListenerContainerFactory(ConsumerFactory<Long, PaymentDtoTransaction> consumerFactory) {
        ConcurrentKafkaListenerContainerFactory<Long, PaymentDtoTransaction> factory = new ConcurrentKafkaListenerContainerFactory<>();

        factory.setConsumerFactory(consumerFactory);
        factory.setConcurrency(3);
        return factory;
    }


}
