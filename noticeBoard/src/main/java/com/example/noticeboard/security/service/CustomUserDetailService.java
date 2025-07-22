package com.example.noticeboard.security.service;

import com.example.noticeboard.common.response.message.AccountMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.noticeboard.account.user.domain.User;
import com.example.noticeboard.account.user.repository.LoginRepository;

@Service
@RequiredArgsConstructor
public class CustomUserDetailService implements UserDetailsService {

	private final LoginRepository loginRepository;
	
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User result = loginRepository.findById(username)
				.orElseThrow(() -> new UsernameNotFoundException(AccountMessage.NOT_FOUNT_ACCOUNT.getMessage()));
		
		return result;
    }
}