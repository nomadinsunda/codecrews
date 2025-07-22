package com.example.noticeboard.account.user.service;

import com.example.noticeboard.account.user.domain.User;
import com.example.noticeboard.account.user.dto.UserResponse;
import com.example.noticeboard.common.response.ResponseCode;
import com.example.noticeboard.common.response.ResponseMessage;
import com.example.noticeboard.common.response.message.AccountMessage;
import com.example.noticeboard.post.post.repository.PostRepository;
import com.example.noticeboard.s3.service.S3Service;
import com.example.noticeboard.security.jwt.dto.Token;
import com.example.noticeboard.security.jwt.service.JwtService;
import com.example.noticeboard.security.jwt.support.CookieSupport;
import com.example.noticeboard.security.jwt.support.JwtTokenProvider;
import com.example.noticeboard.account.user.constant.UserType;
import com.example.noticeboard.account.user.dto.LoginRequest;
import com.example.noticeboard.account.user.dto.PasswordRequest;
import com.example.noticeboard.account.user.exception.LoginException;
import com.example.noticeboard.account.user.repository.LoginRepository;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class LoginService {

	private final LoginRepository loginRepository;
	private final PostRepository postRepository;
	private final JwtTokenProvider jwtTokenProvider;
	private final S3Service s3Service;
	private final JwtService jwtService;
	private final BCryptPasswordEncoder bCryptPasswordEncoder;

	public void validNewAccountVerification(LoginRequest loginRequest) {
		if(loginRepository.findById(loginRequest.getId()).isPresent()) {
			throw new LoginException(AccountMessage.EXISTS_ACCOUNT);
		}

		if(!loginRequest.getPassword().equals(loginRequest.getCheckPassword())) {
			throw new LoginException(AccountMessage.NOT_MATCH_PASSWORD);
		}
	}
	
	public ResponseMessage register(LoginRequest loginRequest , MultipartFile multipartFile) throws IOException {
		validNewAccountVerification(loginRequest);
		String url = s3Service.uploadImageToS3(multipartFile);

		loginRepository.save(User.createGeneralUser(loginRequest , url , bCryptPasswordEncoder.encode(loginRequest.getPassword())));
		return ResponseMessage.of(ResponseCode.REQUEST_SUCCESS);
	}

	public void isValidAccount(LoginRequest request, User user) {
		if(user.getUserType().equals(UserType.OAUTH_USER)) {
			throw new LoginException(AccountMessage.NOT_FOUNT_ACCOUNT);
		}

		if(!user.checkPassword(request.getPassword() , bCryptPasswordEncoder)) {
			throw new LoginException(AccountMessage.NOT_MATCH_PASSWORD);
		}

		if(user.isDelete()) {
			throw new LoginException(AccountMessage.IS_DELETE_ACCOUNT);
		}

		if(user.isSuspension() && user.getSuspensionDate().compareTo(LocalDate.now()) > 0) {
			throw new LoginException("해당 계정은 " + user.getSuspensionDate() + " 일 까지 정지입니다. \n사유 : " + user.getSuspensionReason());
		}
	}
	
	public User login(LoginRequest loginRequest , HttpServletResponse response) {
		User result = findUserById(loginRequest.getId());

		isValidAccount(loginRequest, result);

		result.updateLoginDate();
		createJwtToken(result , response);

		return result;
	}

	public void createJwtToken(User user , HttpServletResponse response) {
		Token token = jwtTokenProvider.createJwtToken(user.getUsername(), user.getRole());
		jwtService.login(token);

		CookieSupport.setCookieFromJwt(token , response);
	}

	public void isExistAccount(String userId, String id) {
		if(loginRepository.findByEmail(userId).isPresent()) {
			throw new LoginException(AccountMessage.EXISTS_EMAIL);
		};

		if(loginRepository.findById(id).isPresent()) {
			throw new LoginException(AccountMessage.EXISTS_ID);
		};
	}

	public ResponseMessage findUserByToken(String token) {
		UserResponse userResponse = UserResponse.createResponse(findUserByAccessToken(token));

		return ResponseMessage.of(ResponseCode.REQUEST_SUCCESS , userResponse);
	}

	public ResponseMessage removeUser(String accessToken , HttpServletResponse response) {
		User result = findUserByAccessToken(accessToken);

		deleteAllS3FilesUploadedByUserId(result.getId());
		result.deleteUser();

		CookieSupport.deleteJwtTokenInCookie(response);

		return ResponseMessage.of(ResponseCode.REQUEST_SUCCESS);
	}

	public void deleteAllS3FilesUploadedByUserId(String userId) {
		List<Long> postIds = postRepository.findPostIdByUserId(userId);

		for(long postId : postIds) {
			s3Service.deleteFileByPostId(postId);
		}
	}

	@Transactional
	public String modifyProfileImage(String accessToken, MultipartFile multipartFile) throws IOException {
		User result = findUserByAccessToken(accessToken);

		if(result.getProfileImage() != null && !result.getProfileImage().isEmpty()) {
			s3Service.deleteFile(result.getProfileImage());
		}

		String url = s3Service.uploadImageToS3(multipartFile);

		result.updateProfileImage(url);

		return url;
	}

	public User findUserByAccessToken(String accessToken) {
		String userId = jwtTokenProvider.getUserPk(accessToken);

		return findUserById(userId);
	}

	public User findUserById(String userId) {
		return loginRepository.findById(userId)
				.orElseThrow(() -> new LoginException(AccountMessage.NOT_FOUNT_ACCOUNT));
	}

	@Transactional
    public void modifyPassword(PasswordRequest request) {
		User user = loginRepository.findByEmail(request.getEmail())
						.orElseThrow(() -> new LoginException(AccountMessage.NOT_FOUNT_ACCOUNT));

		user.updatePassword(bCryptPasswordEncoder.encode(request.getPassword()));
    }
}
