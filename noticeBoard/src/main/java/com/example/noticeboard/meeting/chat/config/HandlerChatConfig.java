package com.example.noticeboard.meeting.chat.config;

import com.example.noticeboard.meeting.chat.handler.WebSocketHandler;
import com.example.noticeboard.security.jwt.support.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@RequiredArgsConstructor
@EnableWebSocket
public class HandlerChatConfig implements WebSocketConfigurer {

    private final WebSocketHandler webSocketHandler;
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(webSocketHandler , "/chat")
                .addInterceptors(new AuthHandshakeInterceptor(jwtTokenProvider))
                .setAllowedOrigins("*");
    }

}

