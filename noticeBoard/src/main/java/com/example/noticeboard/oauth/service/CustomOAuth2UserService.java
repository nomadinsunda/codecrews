package com.example.noticeboard.oauth.service;

import com.example.noticeboard.account.user.constant.UserType;
import com.example.noticeboard.account.user.domain.User;
import com.example.noticeboard.account.user.repository.LoginRepository;
import com.example.noticeboard.oauth.dto.UserSession;
import com.example.noticeboard.oauth.support.OAuthAttributes;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.URI;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final LoginRepository loginRepository;
    private final HttpSession httpSession;

    private final RestOperations restOperations = new RestTemplate();

    private static final ParameterizedTypeReference<Map<String, Object>> PARAMETERIZED_RESPONSE_TYPE =
            new ParameterizedTypeReference<>() {};

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        Assert.notNull(userRequest, "OAuth2UserRequest cannot be null");

        ClientRegistration clientRegistration = userRequest.getClientRegistration();
        String userInfoUri = clientRegistration
                .getProviderDetails()
                .getUserInfoEndpoint()
                .getUri();

        String registrationId = clientRegistration.getRegistrationId();

        if (!StringUtils.hasText(userInfoUri)) {
            throw new OAuth2AuthenticationException("Missing required UserInfo Uri");
        }

        // Access Token
        OAuth2AccessToken accessToken = userRequest.getAccessToken();

        // Create headers
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken.getTokenValue());
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        // Make HTTP Request
        RequestEntity<?> request = new RequestEntity<>(headers, HttpMethod.GET, URI.create(userInfoUri));

        ResponseEntity<Map<String, Object>> response;
        try {
            response = this.restOperations.exchange(request, PARAMETERIZED_RESPONSE_TYPE);
        } catch (RestClientException ex) {
            OAuth2Error error = new OAuth2Error("user_info_request_error",
                    "UserInfo endpoint request error: ",
                    userInfoUri);
            throw new OAuth2AuthenticationException(error, ex);
        }

        Map<String, Object> userAttributes = response.getBody();
        if (userAttributes == null || userAttributes.isEmpty()) {
            throw new OAuth2AuthenticationException(
                    new OAuth2Error("empty_user_info"),
                    "UserInfo response is empty for registrationId: " + registrationId);
        }

        // 사용자 이름 추출 키
        String userNameAttributeName = clientRegistration.getProviderDetails()
                .getUserInfoEndpoint()
                .getUserNameAttributeName();

        OAuthAttributes attributes = OAuthAttributes.of(registrationId, userNameAttributeName, userAttributes);

        User user = saveOrUpdateUser(attributes);
        isValidAccount(user);

        return new DefaultOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority("ROLE_USER")),
                attributes.getAttributes(),
                attributes.getNameAttributeKey());
    }

    public void isValidAccount(User user) {
        if(user.isSuspension() && user.getSuspensionDate().compareTo(LocalDate.now()) > 0) {
            throw new OAuth2AuthenticationException(new OAuth2Error("null"), "해당 계정은 " + user.getSuspensionDate() + " 일 까지 정지입니다. \n사유 : " + user.getSuspensionReason());
        }

        if(user.getUserType() == UserType.GENERAL_USER) {
            throw new OAuth2AuthenticationException(new OAuth2Error("null"), "해당 이메일로 일반 계정이 가입되어있습니다.");
        }

        if(user.isDelete()) {
            throw new OAuth2AuthenticationException(new OAuth2Error("null"), "탈퇴한 사용자입니다.");
        }

        user.updateLoginDate();
    }

    public OAuthAttributes createOauthAttributes(OAuth2UserRequest userRequest) {
        OAuth2UserService delegate = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = delegate.loadUser(userRequest);

        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        String userNameAttributeName = userRequest.getClientRegistration().getProviderDetails()
                .getUserInfoEndpoint().getUserNameAttributeName();

        return OAuthAttributes.of(registrationId, userNameAttributeName, oAuth2User.getAttributes());
    }

    public User saveOrUpdateUser(OAuthAttributes attributes) {
        if(attributes.getEmail() == null) {
            throw new OAuth2AuthenticationException(new OAuth2Error("null"), "계정의 이메일을 찾을 수 없거나, 이메일 수집 여부에 동의하지 않았습니다.");
        }

        return loginRepository.findByEmail(attributes.getEmail())
                .orElseGet(() -> createAndSaveUser(attributes.getEmail()));
    }

    public User createAndSaveUser(String email) {
        User createUser = User.createOAuthUser(getIdWithoutEmail(email), email);

        return loginRepository.save(createUser);
    }

    public String getIdWithoutEmail(String email) {
        return email.split("@")[0];
    }
}
