package net.miraeit.mmrc.domain.user.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.miraeit.mmrc.domain.user.dto.LoginRequest;
import net.miraeit.mmrc.domain.user.dto.LoginResponse;
import net.miraeit.mmrc.domain.user.dto.SignupUserRequest;
import net.miraeit.mmrc.domain.user.dto.SignupUserResponse;
import net.miraeit.mmrc.domain.user.entity.User;
import net.miraeit.mmrc.domain.user.exception.DuplicateUserException;
import net.miraeit.mmrc.domain.user.repository.UserRepository;
import net.miraeit.mmrc.global.authentication.TokenProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;

@SpringBootTest
@Transactional
class UserServiceTest {

	@Autowired ObjectMapper objectMapper;
	@Mock
	UserRepository userRepository;
	@Mock PasswordEncoder passwordEncoder;
	@InjectMocks
	UserService userService;

	@Mock
	TokenProvider tokenProvider;
//	@Autowired AuthenticationManagerBuilder builder;
//	@Value("${jwt.secret}") String secret;


	/**
	 * spring security에서 추상화된 인터페이스 때문에 mock 객체가 제대로 동작하지 않는 듯 (아래 주석 확인)
	 */
	// @Test
	@DisplayName("로그인_admin")
	void login() {
		// given

		LoginRequest loginRequest = LoginRequest.builder()
				.email("admin@miraeit.net")
				.password("admin")
				.build();

		User admin = User.builder()
				.id(Long.valueOf(1))
				.email("admin@miraeit.net")
				.username("admin")
				.phone("123123123")
				.activated(true)
				.build();

		HashMap<String, Object> tokenWithExpirationTime = new HashMap<>();
		tokenWithExpirationTime.put("token", "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhZG1pbkBtaXJhZWl0Lm5ldCIsImF1dGgiOiJST0xFX0FETUlOLFJPTEVfVVNFUiIsImV4cCI6MTY1NTQ1MzU5OH0.5yk-C86tJaZ1yOh9hMwMEmE6S2dZ5HJBl1xzzujNCMM91Bdkg_qVxYiHwZf9WqUQLDDPIgmW29Zz1hWIYd3nKg");
		tokenWithExpirationTime.put("expirationTime", Long.valueOf("1655453598720"));


		given(userRepository.findOneByEmail(any())).willReturn(Optional.of(admin));
		given(tokenProvider.createTokenWithExpirationTime(any())).willReturn(tokenWithExpirationTime);
		given(userRepository.findUsernameByEmail(any())).willReturn(Optional.of("admin"));


		/**
		 * object.providers.userNotFoundEncodedPassword = null
		 * TokenProvider.afterPropertiesSet() 이 동작하지 않아서 그런건가..
		 */
//		AuthenticationManager object = builder.getObject();
//		given(authenticationManagerBuilder.getObject()).willReturn(object); // 여기서 nullPointer exception 뜸.

		// when
		LoginResponse login = userService.login(loginRequest);

		// then
	}

	@Test
	@DisplayName("로그인 실패_UserNotFountException")
	void login_fail() {
		// given
		LoginRequest unknown = new LoginRequest("unknown@miraeit.net", "unknown");

		given(userRepository.findOneByEmail(any())).willReturn(Optional.empty());

		// when
		assertThrows(UsernameNotFoundException.class, () -> {
			LoginResponse login = userService.login(unknown);
		});

		// then
	}


	@Test
	@DisplayName("회원가입")
	@Commit
	void signup() {
		// given
		SignupUserRequest req = new SignupUserRequest("test@miraeit.net", "test", "01028482818");

		SignupUserResponse singupResponse = SignupUserResponse.builder()
				.email("test@miraeit.net")
				.username("test")
				.phone("01028482818")
				.activated(true)
				.build();

		when(userRepository.findSignupUserByEmail(any())).thenReturn(Optional.of(singupResponse));
		when(userRepository.save(any())).thenReturn(new SignupUserResponse("test@miraeit.net", "test", "01028482818",true));
		when(passwordEncoder.encode(any())).thenReturn("test");

		// when
		SignupUserResponse resp = userService.signup(req);

		// then
		assertEquals(req.getEmail(), resp.getEmail());
	}

	@Test
	@DisplayName("회원가입_실패(중복회원)")
	void signup_fail() {
		// given
		SignupUserRequest req = new SignupUserRequest("user@miraeit.net", "user", "01028482818");

		when(userRepository.findUsernameByEmail(any())).thenReturn(Optional.of("user@miraeit.net"));
		when(passwordEncoder.encode(any())).thenReturn("user"); // .password(passwordEncoder.encode(signupUserRequest.getUsername()))
		when(userRepository.save(any())).thenReturn(new SignupUserResponse("user@miraeit.net", "user", "01028482818",true));

		// when
		DuplicateUserException e = assertThrows(DuplicateUserException.class, () -> {
			userService.signup(req);
		});

		// then
	}
}