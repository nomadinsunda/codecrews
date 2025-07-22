package com.example.noticeboard.meeting.chat.repository;

import com.example.noticeboard.meeting.chat.entity.Chat;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatRepository extends JpaRepository<Chat, Long> , CustomChatRepository {

}
