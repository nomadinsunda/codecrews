package com.example.noticeboard.meeting.chat.service;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ChatSessionRegistry {
    // meetingId -> sessions
    private final Map<Long, Set<WebSocketSession>> rooms = new ConcurrentHashMap<>();

    public void join(long meetingId, WebSocketSession session) {
        rooms.computeIfAbsent(meetingId, k -> ConcurrentHashMap.newKeySet()).add(session);
    }

    public void leave(WebSocketSession session) {
        rooms.values().forEach(set -> set.remove(session));
    }

    public Set<WebSocketSession> sessionsOf(long meetingId) {
        return rooms.getOrDefault(meetingId, Collections.emptySet());
    }
}
