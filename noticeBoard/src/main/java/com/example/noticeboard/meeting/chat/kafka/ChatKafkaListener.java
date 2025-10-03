package com.example.noticeboard.meeting.chat.kafka;

import com.example.noticeboard.meeting.chat.dto.ChatRequest;
import com.example.noticeboard.meeting.chat.dto.ChatResponse;
import com.example.noticeboard.meeting.chat.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChatKafkaListener {

    private final ChatService chatService;
    private final SimpMessagingTemplate messaging; // ★

    @KafkaListener(
            topicPattern = "chat-topic-.*",
            groupId = "noticeboard-chat-group",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void onMessage(@Payload ChatRequest incoming) {
        long meetingId = incoming.getMeetingId();

        // 1) 응답 DTO 구성 + DB 저장
        ChatResponse response = ChatResponse.createChatResponse(incoming);
        chatService.saveChat(meetingId, response);

        // 2) STOMP 브로드캐스트 (/topic/chat.{meetingId})
        String dest = "/topic/chat." + meetingId;
        messaging.convertAndSend(dest, response);
    }
}
