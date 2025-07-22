package com.example.noticeboard.meeting.chat.service;

import com.example.noticeboard.meeting.chat.dto.ChatResponse;
import com.example.noticeboard.meeting.chat.entity.ChatRoom;
import com.example.noticeboard.meeting.chat.repository.ChatRepository;
import com.example.noticeboard.meeting.meeting.domain.Meeting;
import com.example.noticeboard.meeting.chat.dto.ChatRoomResponse;
import com.example.noticeboard.security.jwt.support.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatRepository chatRepository;

    private final JwtTokenProvider jwtTokenProvider;

    @Transactional
    public void createChatRoom(Meeting meeting) {
        ChatRoom chatRoom = ChatRoom.createChatRoom(meeting);

        meeting.setChatRoom(chatRoom);
    }

    public List<ChatRoomResponse> findChattingRoomByUserId(String accessToken) {
        String userId = jwtTokenProvider.getUserPk(accessToken);

        return chatRepository.findChatRoomByUserId(userId);
    }

    public List<ChatResponse> findChatDataByMeetingId(long meetingId) {
        return chatRepository.findChatDataByMeetingId(meetingId);
    }

}
