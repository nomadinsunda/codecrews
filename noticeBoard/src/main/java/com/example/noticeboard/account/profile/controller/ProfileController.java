package com.example.noticeboard.account.profile.controller;

import com.example.noticeboard.account.profile.service.ProfileService;
import com.example.noticeboard.common.response.ResponseMessage;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.noticeboard.account.profile.dto.ProfileRequest;

@RestController
@RequestMapping("/profiles")
@RequiredArgsConstructor
public class ProfileController {

	private final ProfileService profileService;
	
	@GetMapping(value = "/statistics")
	public ResponseEntity<ResponseMessage> getStatistics(@CookieValue String accessToken) {
		return ResponseEntity.ok().body(profileService.getStatistics(accessToken));
	}
	
	@PutMapping
	public ResponseEntity<ResponseMessage> updateProfile(@RequestBody ProfileRequest profileRequest , @CookieValue String accessToken , HttpServletResponse response) {

		return ResponseEntity.ok().body(profileService.updateProfile(profileRequest , accessToken , response));
	}

	@GetMapping
	public ResponseEntity<ResponseMessage> getProfileData(@CookieValue String accessToken) {
		return ResponseEntity.ok().body(profileService.getProfileFromUser(accessToken));
	}
}
