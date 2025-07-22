package com.example.noticeboard.admin.login.dto;

import com.example.noticeboard.account.user.domain.User;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AdminLoginResponse {

    private String id;

    private String name;

    public static AdminLoginResponse createResponse(User user) {
        return AdminLoginResponse.builder()
                .id(user.getId())
                .name(user.getUsername())
                .build();
    }

}
