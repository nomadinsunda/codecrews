package com.example.noticeboard.meeting.chat.service;

import com.example.noticeboard.meeting.chat.dto.ChatRequest;
import com.example.noticeboard.meeting.chat.dto.ChatResponse;
import com.example.noticeboard.meeting.chat.service.ChatService;
import com.example.noticeboard.meeting.chat.service.ChatSessionRegistry;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

@Service
@RequiredArgsConstructor
public class ChatKafkaListener {

    private final ChatSessionRegistry sessionRegistry;
    private final ChatService chatService; // DB 저장용
    private final ObjectMapper objectMapper = new ObjectMapper();

    // 모든 chat-topic-<id>를 청취
    @KafkaListener(
            topicPattern = "chat-topic-.*",
            groupId = "noticeboard-chat-group",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void onMessage(@Payload ChatRequest incoming) {
        try {
            long meetingId = incoming.getMeetingId();

            // 1) 응답 DTO 구성 및 DB 저장
            ChatResponse response = ChatResponse.createChatResponse(incoming);
            chatService.saveChat(meetingId, response); // ChatService에 저장 메서드 추가 (아래 7) 참고)

            // 2) 같은 room(meetingId) 세션에게만 브로드캐스트
            String payload = objectMapper.writeValueAsString(response);
            TextMessage text = new TextMessage(payload);

            for (WebSocketSession session : sessionRegistry.sessionsOf(meetingId)) {
                if (session.isOpen()) {
                    session.sendMessage(text);
                }
            }
        } catch (Exception e) {
            // 로깅만
            e.printStackTrace();
        }
    }
}
