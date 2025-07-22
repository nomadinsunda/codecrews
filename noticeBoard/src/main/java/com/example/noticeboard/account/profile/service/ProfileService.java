package com.example.noticeboard.account.profile.service;

import com.example.noticeboard.account.user.domain.User;
import com.example.noticeboard.account.user.service.LoginService;
import com.example.noticeboard.common.response.ResponseCode;
import com.example.noticeboard.common.response.ResponseMessage;
import com.example.noticeboard.common.response.message.AccountMessage;
import com.example.noticeboard.security.jwt.support.CookieSupport;
import com.example.noticeboard.security.jwt.support.JwtTokenProvider;
import com.example.noticeboard.account.profile.dto.StatisticsResponse;
import com.example.noticeboard.account.profile.dto.ProfileRequest;
import com.example.noticeboard.account.user.exception.LoginException;
import com.example.noticeboard.account.user.repository.LoginRepository;
import com.example.noticeboard.account.profile.repository.ProfileRepository;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.example.noticeboard.account.profile.dto.ProfileResponse.createProfileResponse;

@Service
@RequiredArgsConstructor
public class ProfileService {

    private final ProfileRepository profileRepository;
	private final LoginRepository loginRepository;
	private final JwtTokenProvider jwtTokenProvider;
	private final LoginService loginService;
	
	public ResponseMessage<StatisticsResponse> getStatistics(String accessToken) {
		String userId = jwtTokenProvider.getUserPk(accessToken);

		return ResponseMessage.of(ResponseCode.REQUEST_SUCCESS, profileRepository.getStatisticsOfUser(userId));
	}

	@Transactional
	public ResponseMessage updateProfile(ProfileRequest profileRequest , String token , HttpServletResponse response) {
		User user = loginService.findUserByAccessToken(token);

		if(!profileRequest.getUserId().equals(user.getId())) {
			if(loginRepository.findById(profileRequest.getUserId()).isPresent()) {
				throw new LoginException(AccountMessage.EXISTS_ID);
			}

			user.updateId(profileRequest.getUserId());

			CookieSupport.deleteJwtTokenInCookie(response);
		}

		user.getProfile().updateProfile(profileRequest);

		return ResponseMessage.of(ResponseCode.REQUEST_SUCCESS);
	}

	public ResponseMessage getProfileFromUser(String token) {
		User result = loginService.findUserByAccessToken(token);

		return ResponseMessage.of(ResponseCode.REQUEST_SUCCESS, createProfileResponse(result.getProfile() , result));
	}
}
