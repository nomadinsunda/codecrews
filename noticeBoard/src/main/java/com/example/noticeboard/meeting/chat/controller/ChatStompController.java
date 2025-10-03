package com.example.noticeboard.meeting.chat.controller;

import com.example.noticeboard.meeting.chat.dto.ChatRequest;
import com.example.noticeboard.meeting.chat.dto.MessageType;
import com.example.noticeboard.meeting.chat.kafka.ChatKafkaProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class ChatStompController {

    private final ChatKafkaProducer producer;

    // 클라이언트가 /app/chat.send.{meetingId} 로 전송
    @MessageMapping("chat.send.{meetingId}")
    public void send(@DestinationVariable long meetingId, ChatRequest payload) {
        // 방 파라미터 보정
        payload.setMeetingId(meetingId);

        // 입장 이벤트도 Kafka로 흘리고, Listener에서 브로드캐스트/저장 일원화
        if (payload.getMessageType() == null) {
            payload.setMessageType(MessageType.SEND);
        }
        producer.publish(payload);
    }

    // 필요하면 join 전용 엔드포인트도 가능 (/app/chat.enter.{meetingId})
    @MessageMapping("chat.enter.{meetingId}")
    public void enter(@DestinationVariable long meetingId, ChatRequest payload) {
        payload.setMeetingId(meetingId);
        payload.setMessageType(MessageType.ENTER);
        producer.publish(payload);
    }
}

