package com.example.noticeboard.meeting.chat.interceptor;

import com.example.noticeboard.security.jwt.support.JwtTokenProvider;
import jakarta.servlet.http.Cookie;
import org.springframework.http.HttpStatus;
import org.springframework.web.socket.server.HandshakeInterceptor;
import org.springframework.web.socket.server.HandshakeHandler;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.server.ServletServerHttpRequest;

import java.util.Map;

public class AuthHandshakeInterceptor implements HandshakeInterceptor {

    private final JwtTokenProvider jwtTokenProvider;

    // 생성자 주입 (스프링 빈 등록 시 JwtTokenProvider 자동 주입됨)
    public AuthHandshakeInterceptor(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }


    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
                                   WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
        if (request instanceof ServletServerHttpRequest) {
            HttpServletRequest servletRequest = ((ServletServerHttpRequest) request).getServletRequest();
            Cookie[] cookies = servletRequest.getCookies();
            if (cookies != null) {
                for (Cookie c : cookies) {
                    if ("accessToken".equals(c.getName())) {
                        String token = c.getValue();
                        // 토큰 검증 로직 (예: JWT 검증)
                        String userId = validateAndGetUserId(token);
                        if (userId != null) {
                            attributes.put("userId", userId); // 세션/연결에 사용자 바인딩
                            return true;
                        }
                    }
                }
            }
        }
        response.setStatusCode(HttpStatus.FORBIDDEN);
        return false;
    }

    /**
     * JWT 액세스 토큰을 검증하고 userId(subject)를 반환
     * 검증 실패 시 null 반환
     */
    private String validateAndGetUserId(String token) {
        try {
            boolean valid = jwtTokenProvider.validateAccessToken(token);
            if (valid) {
                return jwtTokenProvider.getUserPk(token); // subject = User.id
            }
        } catch (Exception e) {
            // 로그만 찍고 null 반환
            System.out.println("JWT validation failed: " + e.getMessage());
        }
        return null;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
                               WebSocketHandler wsHandler, Exception ex) { }
}
