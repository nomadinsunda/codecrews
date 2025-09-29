package com.example.noticeboard.meeting.chat.repository;

import com.example.noticeboard.meeting.chat.entity.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
}