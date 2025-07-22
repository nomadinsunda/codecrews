package com.example.noticeboard.meeting.chat.controller;

import com.example.noticeboard.meeting.chat.dto.ChatResponse;
import com.example.noticeboard.meeting.chat.service.ChatService;
import com.example.noticeboard.meeting.chat.dto.ChatRoomResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/chat")
public class ChatController {

    private final ChatService chatService;

    @GetMapping("/{meetingId}")
    public ResponseEntity findChatDataById(@PathVariable long meetingId) {
        List<ChatResponse> result = chatService.findChatDataByMeetingId(meetingId);

        return ResponseEntity.ok().body(result);
    }

    @GetMapping("/participants")
    public ResponseEntity findChattingRoomByUserId(@CookieValue String accessToken) {
        List<ChatRoomResponse> result = chatService.findChattingRoomByUserId(accessToken);

        return ResponseEntity.ok().body(result);
    }

}
