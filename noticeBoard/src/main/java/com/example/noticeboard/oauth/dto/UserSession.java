package com.example.noticeboard.oauth.dto;

import com.example.noticeboard.account.user.domain.User;

public class UserSession {

    private final String email;

    private UserSession(User user) {
        this.email = user.getId();
    }

    public static UserSession of(User user) {
        return new UserSession(user);
    }

    public String getId() {
        return email;
    }
}
