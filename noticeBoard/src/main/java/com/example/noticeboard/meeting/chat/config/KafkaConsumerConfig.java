package com.example.noticeboard.meeting.chat.config;

import com.example.noticeboard.meeting.chat.dto.ChatRequest;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;

@Configuration
@RequiredArgsConstructor
public class KafkaConsumerConfig {

    private final KafkaProperties kafkaProperties;

    // Kafka Consumer Factory Bean
    @Bean
    public ConsumerFactory<String, ChatRequest> consumerFactory() {
        // application.yml의 설정을 가져옵니다.
        return new DefaultKafkaConsumerFactory<>(kafkaProperties.buildConsumerProperties());
    }

    // Kafka Listener Container Factory Bean (KafkaListener에서 사용됨)
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, ChatRequest> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, ChatRequest> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        // 메시지 타입이 'chat-topic-.*' 패턴 토픽에서만 생성되도록 처리
        factory.setBatchListener(false); // 단일 메시지 처리
        return factory;
    }
}