package com.example.noticeboard.meeting.chat.kafka;

import com.example.noticeboard.meeting.chat.dto.ChatRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ChatKafkaProducer {

    private final KafkaTemplate<String, ChatRequest> kafkaTemplate;

    public void publish(ChatRequest message) {
        String topic = topicOf(message.getMeetingId());
        kafkaTemplate.send(topic, message);
    }

    public static String topicOf(long meetingId) {
        return "chat-topic-" + meetingId;
    }
}
