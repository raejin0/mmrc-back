package net.miraeit.mmrc.domain.user.service;

import lombok.RequiredArgsConstructor;
import net.miraeit.mmrc.domain.user.dto.LoginRequest;
import net.miraeit.mmrc.domain.user.dto.LoginResponse;
import net.miraeit.mmrc.domain.user.dto.SignupUserRequest;
import net.miraeit.mmrc.domain.user.dto.SignupUserResponse;
import net.miraeit.mmrc.domain.user.entity.Authority;
import net.miraeit.mmrc.domain.user.entity.User;
import net.miraeit.mmrc.domain.user.exception.DuplicateUserException;
import net.miraeit.mmrc.domain.user.repository.UserRepository;
import net.miraeit.mmrc.global.authentication.TokenProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.HashMap;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

	private final TokenProvider tokenProvider;
	private final AuthenticationManagerBuilder authenticationManagerBuilder;

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;

	@Value("${initial.password}") String initialPassword;

	public LoginResponse login(LoginRequest loginRequest) {
		// 아이디 존재 유무 확인
		Optional<User> oneByEmail = userRepository.findOneByEmail(loginRequest.getEmail());
		oneByEmail.orElseThrow(() -> new UsernameNotFoundException("Failed to find user '" + loginRequest.getEmail() + "'"));

		// 인증토큰 생성
		UsernamePasswordAuthenticationToken authenticationToken =
				new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword());

		// 아이디 및 비밀번호 확인
		Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

		SecurityContextHolder.getContext().setAuthentication(authentication);

		// get token and expirationTime
		HashMap<String, Object> tokenWithExpirationTime = tokenProvider.createTokenWithExpirationTime(authentication);
		String jwt = (String) tokenWithExpirationTime.get("token");
		long expirationTime = (long) tokenWithExpirationTime.get("expirationTime") / 1000;

		// get username
		Optional<String> findUser = userRepository.findUsernameByEmail(loginRequest.getEmail());
		String username = "";
		if(findUser.isPresent()) username = findUser.get();

		// build loginResponse
		LoginResponse loginResponse = LoginResponse.builder()
				.token(jwt)
				.username(username)
				.expirationTime(expirationTime)
				.build();

		return loginResponse;
	}

	@Transactional
	public SignupUserResponse signup(SignupUserRequest signupUserRequest){
		if(userRepository.findUsernameByEmail(signupUserRequest.getEmail()).orElse(null) != null)
			throw new DuplicateUserException();

		Authority authority = Authority.builder()
				.authorityName("ROLE_USER")
				.build();

		User signupUser = User.builder()
				.email(signupUserRequest.getEmail())
				.username(signupUserRequest.getUsername())
				.password(passwordEncoder.encode(initialPassword))
				.phone(signupUserRequest.getPhone())
				.activated(true)
				.authorities(Collections.singleton(authority)) // 추후 로직 확인
				.build();

		userRepository.createUser(signupUser);

		return userRepository.findSignupUserByEmail(signupUser.getEmail()).get();
	}
}