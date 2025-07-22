package com.example.noticeboard.meeting.chat.repository;

import com.example.noticeboard.meeting.chat.dto.ChatResponse;
import com.example.noticeboard.meeting.chat.dto.ChatRoomResponse;

import java.util.List;

public interface CustomChatRepository {

    List<ChatResponse> findChatDataByMeetingId(long meetingId);

    List<ChatRoomResponse> findChatRoomByUserId(String userId);

}
