package com.example.noticeboard.meeting.chat.interceptor;

import com.example.noticeboard.security.jwt.support.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;

import java.security.Principal;

@RequiredArgsConstructor
public class JwtChannelInterceptor implements ChannelInterceptor {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor acc = StompHeaderAccessor.wrap(message);
        if (StompCommand.CONNECT.equals(acc.getCommand())) {
            // Authorization: Bearer <token> or cookie 미사용 환경 대비
            String bearer = acc.getFirstNativeHeader("Authorization");
            String token = null;
            if (bearer != null && bearer.startsWith("Bearer ")) {
                token = bearer.substring(7);
            }
            // Handshake에서 attributes로 넣은 userId를 가져올 수도 있음.
            if (token != null && jwtTokenProvider.validateAccessToken(token)) {
                String userId = jwtTokenProvider.getUserPk(token);
                acc.setUser(new StompPrincipal(userId));
            } else if (acc.getSessionAttributes() != null && acc.getSessionAttributes().get("userId") != null) {
                String userId = (String) acc.getSessionAttributes().get("userId");
                acc.setUser(new StompPrincipal(userId));
            } else {
                throw new IllegalArgumentException("Unauthorized STOMP CONNECT");
            }
        }
        return message;
    }

    // 간단한 Principal 구현
    static class StompPrincipal implements Principal {
        private final String name;
        StompPrincipal(String name){ this.name = name; }
        @Override public String getName(){ return name; }
    }
}

