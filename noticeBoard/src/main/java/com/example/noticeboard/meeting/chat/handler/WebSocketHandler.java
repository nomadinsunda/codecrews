package com.example.noticeboard.meeting.chat.handler;

import com.example.noticeboard.meeting.chat.dto.ChatRequest;
import com.example.noticeboard.meeting.chat.dto.ChatResponse;
import com.example.noticeboard.meeting.chat.entity.Chat;
import com.example.noticeboard.meeting.chat.repository.ChatRepository;
import com.example.noticeboard.meeting.chat.service.ChatKafkaProducer;
import com.example.noticeboard.meeting.chat.service.ChatSessionRegistry;
import com.example.noticeboard.meeting.meeting.domain.Meeting;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.example.noticeboard.meeting.chat.dto.MessageType;
import com.example.noticeboard.meeting.meeting.exception.MeetingException;
import com.example.noticeboard.meeting.meeting.repository.meeting.MeetingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.example.noticeboard.common.response.message.MeetingMessage.NOT_FOUND_MEETING;

@Component
@RequiredArgsConstructor
public class WebSocketHandler extends TextWebSocketHandler {

//    private Map<Long , List<WebSocketSession>> sessionList = new HashMap<>();
//    private final MeetingRepository meetingRepository;
//    private final ChatRepository chatRepository;

    private final ChatSessionRegistry sessionRegistry;
    private final ChatKafkaProducer producer;

    @Override
    @Transactional
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        ChatRequest chatMessage = mapper.readValue(message.getPayload(), ChatRequest.class);
        long meetingId = chatMessage.getMeetingId();

        if (chatMessage.getMessageType() == MessageType.ENTER) {
            // 세션 입장 시 등록만 수행 (브로드캐스트는 필요 없음)
            sessionRegistry.join(meetingId, session);
        } else if (chatMessage.getMessageType() == MessageType.SEND) {
            // 메시지 전송 시에는 오직 Kafka로 발행만 수행
            producer.publish(chatMessage);
        }
    }

//    private void joinChatBySession(long meetingId , WebSocketSession session) {
//        if(!sessionList.containsKey(meetingId)) {
//            sessionList.put(meetingId , new ArrayList<>());
//        }
//        List<WebSocketSession> sessions = sessionList.get(meetingId);
//        if(!sessions.contains(session)) {
//            sessions.add(session);
//        }
//    }
//
//    private void sendChatToSameRootId(long meetingId , ObjectMapper objectMapper , ChatRequest chatMessage) throws IOException {
//        List<WebSocketSession> sessions = sessionList.get(meetingId);
//        ChatResponse chatResponse = ChatResponse.createChatResponse(chatMessage);
//        saveChatData(meetingId , chatResponse);
//
//        for(WebSocketSession webSocketSession : sessions) {
//            String result = objectMapper.writeValueAsString(chatResponse);
//            webSocketSession.sendMessage(new TextMessage(result));
//        }
//    }
//
//    private void saveChatData(long meetingId , ChatResponse chatResponse) {
//        Meeting meeting = meetingRepository.findMeetingAndChatById(meetingId)
//                .orElseThrow(() -> new MeetingException(NOT_FOUND_MEETING));
//
//        Chat chat = Chat.createChat(chatResponse , meeting);
//        meeting.getChats().add(chat);
//        chatRepository.save(chat);
//    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        sessionRegistry.leave(session);
    }
}
