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
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
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

        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String email = (String) oAuth2User.getAttributes().get("email");

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
