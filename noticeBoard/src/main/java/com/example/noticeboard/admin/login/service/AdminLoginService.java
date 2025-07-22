package com.example.noticeboard.admin.login.service;

import com.example.noticeboard.account.user.domain.User;
import com.example.noticeboard.account.user.service.LoginService;
import com.example.noticeboard.admin.login.dto.AdminLoginRequest;
import com.example.noticeboard.admin.login.dto.AdminLoginResponse;
import com.example.noticeboard.common.response.message.AccountMessage;
import com.example.noticeboard.account.user.constant.UserRole;
import com.example.noticeboard.account.user.dto.LoginRequest;
import com.example.noticeboard.account.user.exception.LoginException;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminLoginService {

    private final LoginService loginService;

    public AdminLoginResponse adminLogin(AdminLoginRequest request, HttpServletResponse response) {
        LoginRequest loginRequest = LoginRequest.createLoginRequest(request);

        User result = loginService.login(loginRequest, response);

        if(result.getRole() != UserRole.MANAGER) {
            throw new LoginException(AccountMessage.NOT_FOUNT_ACCOUNT);
        }

        return AdminLoginResponse.createResponse(result);
    }

}
