package com.example.noticeboard.meeting.chat.config;

import com.example.noticeboard.meeting.chat.dto.ChatRequest;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.*;

import java.util.Map;

@Configuration
public class KafkaProducerConfig {

    private final org.springframework.boot.autoconfigure.kafka.KafkaProperties props;

    public KafkaProducerConfig(org.springframework.boot.autoconfigure.kafka.KafkaProperties props) {
        this.props = props;
    }

    @Bean
    public ProducerFactory<String, ChatRequest> chatProducerFactory() {
        Map<String, Object> cfg = props.buildProducerProperties();
        cfg.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        cfg.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, org.springframework.kafka.support.serializer.JsonSerializer.class);
        return new DefaultKafkaProducerFactory<>(cfg);
    }

    @Bean
    public KafkaTemplate<String, ChatRequest> chatKafkaTemplate() {
        return new KafkaTemplate<>(chatProducerFactory());
    }
}
