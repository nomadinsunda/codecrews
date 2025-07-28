package com.example.noticeboard.oauth.support;

import com.example.noticeboard.account.user.constant.UserRole;
import com.example.noticeboard.account.user.domain.User;
import com.example.noticeboard.account.user.repository.LoginRepository;
import com.example.noticeboard.oauth.dto.UserSession;
import com.example.noticeboard.security.jwt.dto.Token;
import com.example.noticeboard.security.jwt.support.JwtTokenProvider;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Optional;

import static com.example.noticeboard.security.jwt.support.CookieSupport.*;

@Component
@EqualsAndHashCode(callSuper = false)
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    private final LoginRepository loginRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final HttpSession httpSession;

    @Value("${client.url}")
    private String clientUrl;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        String email = null;

        if (authentication instanceof OAuth2AuthenticationToken) {
            OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
            String registrationId = oauthToken.getAuthorizedClientRegistrationId(); // ex: "google", "kakao", etc.

            OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();

            if ("naver".equals(registrationId)) {
                LinkedHashMap<String, String> responseAttr = (LinkedHashMap<String, String>) oAuth2User.getAttributes().get("response");
                email = responseAttr.get("email");

            } else if ("google".equals(registrationId)) {
                email = (String) oAuth2User.getAttributes().get("email");

            } else if ("kakao".equals(registrationId)) {
                LinkedHashMap<String, String> reponsekakao = (LinkedHashMap<String, String>) oAuth2User.getAttributes().get("kakao_account");
                email = reponsekakao.get("email");

            } if(email == null) {
                throw new OAuth2AuthenticationException(
                        new OAuth2Error("email_not_found"),
                        "OAuth2 로그인에 실패했습니다. 이메일 정보를 가져올 수 없습니다."
                );
            }
        }

        if (email == null) {
            getRedirectStrategy().sendRedirect(request, response, createRedirectUrl(clientUrl + "/oauth2/disallowance"));
            return;
        }

        Optional<User> user = loginRepository.findByEmail(email);
        Token token = jwtTokenProvider.createJwtToken(user.get().getId(), UserRole.USER);

        HttpSession session = request.getSession();
        if (session != null) {
            session.invalidate();
        }

        setCookieFromJwt(token , response);
        getRedirectStrategy().sendRedirect(request, response, createRedirectUrl(clientUrl));
    }

    public String createRedirectUrl(String url) {
        return UriComponentsBuilder.fromUriString(url).build().toUriString();
    }
}
