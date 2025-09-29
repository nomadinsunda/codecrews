package com.example.noticeboard.meeting.chat.service;

import com.example.noticeboard.meeting.chat.dto.ChatResponse;
import com.example.noticeboard.meeting.chat.entity.ChatRoom;
import com.example.noticeboard.meeting.chat.repository.ChatRepository;
import com.example.noticeboard.meeting.chat.repository.ChatRoomRepository;
import com.example.noticeboard.meeting.meeting.domain.Meeting;
import com.example.noticeboard.meeting.chat.dto.ChatRoomResponse;
import com.example.noticeboard.meeting.meeting.repository.meeting.MeetingRepository;
import com.example.noticeboard.security.jwt.support.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.noticeboard.meeting.chat.entity.Chat;
import com.example.noticeboard.meeting.meeting.exception.MeetingException;

import java.util.List;

import static com.example.noticeboard.meeting.chat.dto.MeetingMessage.NOT_FOUND_MEETING;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatRepository chatRepository;
    private final ChatRoomRepository chatRoomRepository; // ChatRoomRepository 주입
    private final MeetingRepository meetingRepository;
    private final JwtTokenProvider jwtTokenProvider;

    @Transactional
    public void createChatRoom(Meeting meeting) {
        ChatRoom chatRoom = ChatRoom.createChatRoom(meeting);
        meeting.setChatRoom(chatRoom);
        chatRoomRepository.save(chatRoom);
    }

    public List<ChatRoomResponse> findChattingRoomByUserId(String accessToken) {
        String userId = jwtTokenProvider.getUserPk(accessToken);

        return chatRepository.findChatRoomByUserId(userId);
    }

    public List<ChatResponse> findChatDataByMeetingId(long meetingId) {
        return chatRepository.findChatDataByMeetingId(meetingId);
    }


    @Transactional
    public void saveChat(long meetingId, ChatResponse chatResponse) {
        Meeting meeting = meetingRepository.findMeetingAndChatById(meetingId)
                .orElseThrow(() -> new MeetingException(NOT_FOUND_MEETING));

        Chat chat = Chat.createChat(chatResponse, meeting);
        meeting.getChats().add(chat);
        chatRepository.save(chat);
    }

}
