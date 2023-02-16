package net.miraeit.mmrc.domain.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.miraeit.mmrc.domain.user.dto.*;
import net.miraeit.mmrc.domain.user.exception.DuplicateUserException;
import net.miraeit.mmrc.domain.user.repository.UserRepository;
import net.miraeit.mmrc.domain.user.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.nio.charset.Charset;
import java.util.ArrayList;

import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {

	@Autowired ObjectMapper objectMapper;
	@Autowired MockMvc mockMvc;
	@MockBean
	UserService userService;
	@MockBean
	UserRepository userRepository;

	protected MediaType contentType = new MediaType(
			MediaType.APPLICATION_JSON.getType(),
			MediaType.APPLICATION_JSON.getSubtype(),
			Charset.forName("utf8")
	);

	@Test
	@DisplayName("로그인 및 권한검증")
	void login() throws Exception {
		// given
		LoginRequest admin = new LoginRequest("admin@miraeit.net", "admin");
		LoginResponse loginResponse = LoginResponse.builder()
				.token("eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhZG1pbkBtaXJhZWl0Lm5ldCIsImF1dGgiOiJST0xFX0FETUlOLFJPTEVfVVNFUiIsImV4cCI6MTY1NTM4MzMxOX0.YM-f6Agwridsg57Wv5XHdTHVX_YY0YrjjSogqWdKXvJ2qmgcKdgCWw_UnIxhaUfb3wtR3k4m7kK6zIztnj2Nog")
				.username("admin")
				.expirationTime(1655383319)
				.build();

		given(userService.login(any())).willReturn(loginResponse);

		// when
		ResultActions resultActions = mockMvc
				.perform(post("/api/login")
						.contentType(this.contentType)
						.content(objectMapper.writeValueAsString(admin))
				)
				.andDo(print());

		// then
		resultActions.andExpectAll(
						status().isOk(),
						content().contentType(MediaType.APPLICATION_JSON),
						jsonPath("$.*", hasSize(2)),
						jsonPath("$.success").value("true"),
						jsonPath("$.data.*", hasSize(3)),
						jsonPath("$.data.token").isNotEmpty(),
						jsonPath("$.data.username").value("admin"),
						jsonPath("$.data.expirationTime").isNotEmpty());
	}

	@Test
	@DisplayName("로그인 및 권한검증_실패")
	void login_fail() throws Exception {
		// given
		LoginRequest unknown = new LoginRequest("unknown@miraeit.net", "unknown");

		given(userService.login(any())).willThrow(UsernameNotFoundException.class);

		// when
		ResultActions resultActions =
				mockMvc.perform(post("/api/login")
								.contentType(this.contentType)
								.content(objectMapper.writeValueAsString(unknown)))
						.andDo(print());

		// then
		resultActions.andExpectAll(
				status().isNotFound(),
				content().contentType(MediaType.APPLICATION_JSON),
				jsonPath("$.*", hasSize(3)),
				jsonPath("$.success").value("false"),
				jsonPath("$.code").value("U0001")
		);
	}

	@Test
	@DisplayName("회원목록 조회")
	@WithMockUser(roles = {"ADMIN"})
	void findAllUsers() throws Exception {
		// given
		ArrayList<UserListResponse> allUsers = new ArrayList<>();

		for (int i = 0; i < 2; i++) {
			UserListResponse response = UserListResponse.builder()
					.email(String.valueOf(i))
					.username(String.valueOf(i))
					.phone(String.valueOf(i))
					.activated(true)
					.build();
			allUsers.add(response);
		}

		given(userRepository.findAllUsers()).willReturn(allUsers);

		// when
		ResultActions resultActions =
				mockMvc.perform(get("/api/users")
								.contentType(this.contentType))
						.andDo(print());

		// then
		resultActions.andExpectAll(
				status().isOk(),
				content().contentType(MediaType.APPLICATION_JSON),
				jsonPath("$.*", hasSize(2)),
				jsonPath("$.success").value("true"),
				jsonPath("$.data.*", hasSize(2)));
	}

	@Test
	@DisplayName("회원가입")
	@WithMockUser(roles = {"ADMIN"})
	void signup() throws Exception {
		// given
		SignupUserRequest signupUserRequest = SignupUserRequest.builder()
				.email("test@miraeit.net")
				.username("test")
				.phone("010102938711")
				.build();

		SignupUserResponse response = SignupUserResponse.builder()
				.email("test@miraeit.net")
				.username("test")
				.phone("010102938711")
				.activated(true)
				.build();

		given(userService.signup(any())).willReturn(response);

		// when
		ResultActions resultActions =
				mockMvc.perform(post("/api/signup")
								.contentType(this.contentType)
								.content(objectMapper.writeValueAsString(signupUserRequest)))
						.andDo(print());

		// then
		resultActions.andExpectAll(
				status().isOk(),
				content().contentType(MediaType.APPLICATION_JSON),
				jsonPath("$.*", hasSize(2)),
				jsonPath("$.success").value("true"),
				jsonPath("$.data.*", hasSize(4))
		);
	}

	@Test
	@WithMockUser(username = "admin@miraeit.net", roles = {"ADMIN"})
	void signupDuplicate() throws Exception {
		// given
		SignupUserRequest signupUserRequest = SignupUserRequest.builder()
				.email("user@miraeit.net")
				.username("user")
				.phone("010102938711")
				.build();

		given(userService.signup(any())).willThrow(DuplicateUserException.class);

		// when
		ResultActions resultActions =
				mockMvc.perform(post("/api/signup")
							.contentType(this.contentType)
							.content(objectMapper.writeValueAsString(signupUserRequest)))
						.andDo(print());
		// then
		resultActions.andExpectAll(
				status().isConflict(),
				content().contentType(MediaType.APPLICATION_JSON),
				jsonPath("$.*", hasSize(3)),
				jsonPath("$.success").value("false"),
				jsonPath("$.code").value("U0000")
		);
	}
}