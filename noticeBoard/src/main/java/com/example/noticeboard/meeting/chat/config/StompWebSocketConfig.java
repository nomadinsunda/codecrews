package com.example.noticeboard.meeting.chat.config;


import com.example.noticeboard.meeting.chat.interceptor.JwtHandshakeInterceptor;
import com.example.noticeboard.meeting.chat.interceptor.JwtChannelInterceptor;
import com.example.noticeboard.security.jwt.support.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.*;

@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class StompWebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws") // 클라이언트 접속 엔드포인트
                .addInterceptors(new JwtHandshakeInterceptor(jwtTokenProvider))
                .setAllowedOriginPatterns("*")
                .withSockJS(); // 필요 시 제거 가능
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // 클라이언트 구독 prefix
        registry.enableSimpleBroker("/topic");
        // 서버 수신 prefix (@MessageMapping)
        registry.setApplicationDestinationPrefixes("/app");
        // 사용자 큐(개별 전송 필요 시)
        registry.setUserDestinationPrefix("/user");
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(new JwtChannelInterceptor(jwtTokenProvider));
    }
}

