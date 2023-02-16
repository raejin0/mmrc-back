package net.miraeit.mmrc.domain.user.controller;

import lombok.AllArgsConstructor;
import net.miraeit.mmrc.domain.user.dto.*;
import net.miraeit.mmrc.global.dto.SuccessResponse;
import net.miraeit.mmrc.domain.user.repository.UserRepository;
import net.miraeit.mmrc.domain.user.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api")
public class UserController {

	private final UserRepository userRepository;
	private final UserService userService;

	@PostMapping("/login")
	public ResponseEntity<SuccessResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
		// String user = new BCrypt().hashpw("user", "$2a$08$UkVvwpULis18S19S5pZFn.YHPZt3oaqHZnDwqbCW9pft6uFtkXKDC");
		LoginResponse loginResponse = userService.login(loginRequest);
		return ResponseEntity.ok(new SuccessResponse(loginResponse));
	}

	// TODO @MocMVC로 다시 테스트
	/**
	 * user management
	 */
	@GetMapping("/users")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<SuccessResponse> findAllUsers(){
		List<UserListResponse> allUsers = userRepository.findAllUsers();
		return ResponseEntity.ok(new SuccessResponse(allUsers));
	}

	@PostMapping("/signup")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<SuccessResponse> signup(@Valid @RequestBody SignupUserRequest signupUserRequest) {
		return ResponseEntity.ok(new SuccessResponse(userService.signup(signupUserRequest))); // 201 상태코드는 새로운 url이 생성되고 그 url로 redirect할 경우 사용되는 듯
	}
}
