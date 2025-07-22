package com.example.noticeboard.security.jwt.dto;

import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Token {
    private String grantType;
    private String accessToken;
    private String refreshToken;
    private String key;
}