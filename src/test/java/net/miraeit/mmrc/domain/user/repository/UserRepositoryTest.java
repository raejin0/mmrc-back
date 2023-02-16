package net.miraeit.mmrc.domain.user.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.miraeit.mmrc.domain.user.dto.UserListResponse;
import net.miraeit.mmrc.domain.user.entity.Authority;
import net.miraeit.mmrc.domain.user.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;


@SpringBootTest
@Transactional
class UserRepositoryTest {

	@Autowired ObjectMapper objectMapper;
	@Autowired
	UserRepository userRepository;
	@Autowired PasswordEncoder passwordEncoder;

	@Test
	@DisplayName("회원조회(권한 포함) by 이메일정상")
	void findOneWithAuthoritiesByEmail() {
		// given
		String admin = "admin@miraeit.net";

		// when
		Optional<User> findAdmin = userRepository.findOneWithAuthoritiesByEmail(admin);

		// then
		assertThat(findAdmin.get().getEmail()).isEqualTo(admin);
	}

	@Test
	@DisplayName("회원조회(권한 포함) by 이메일실패")
		void findOneWithAuthoritiesByEmail_fail() {
		// given
		String unknown = "unknown@miraeit.net";

		// when
		assertThrows(NoSuchElementException.class, () -> {
			userRepository.findOneWithAuthoritiesByEmail(unknown).get();
		});

		// then
	}


	@Test
	@DisplayName("회원이름 조회 by 이메일(권한 포함)")
	void findUsernameByEmail() {
		// given
		String admin = "admin@miraeit.net";

		// when
		Optional<String> findAdmin = userRepository.findUsernameByEmail(admin);

		// then
		assertEquals("admin", findAdmin.get());
	}

	@Test
	@DisplayName("회원이름 조회 by 이메일(권한 포함)_실패")
	void findUsernameByEmail_fail() {
		// given
		String unknown = "unknown@miraeit.net";

		// when
		assertThrows(NoSuchElementException.class, () -> {
			userRepository.findUsernameByEmail(unknown).get();
		});

		// then
	}

	@Test
	@DisplayName("전체 회원 조회")
	void findAllMembers() {
		// when
		List<UserListResponse> all = userRepository.findAllUsers();

		// then
		assertEquals("admin", all.get(0).getUsername());
		assertEquals("user", all.get(1).getUsername());
		assertEquals(all.size(), 2);
	}

	@Test
	@DisplayName("회원 저장")
	void saveUser() {
		// given
		Authority authority = Authority.builder()
			.authorityName("ROLE_USER")
			.build();

		User user = User.builder()
			.email("test@miraeit.net")
			.username("test")
			.password(passwordEncoder.encode("test"))
			.phone("01021234122")
			.activated(true)
			.authorities(Collections.singleton(authority)) // 추후 로직 확인
			.build();

		// when
		userRepository.createUser(user);

		// then
		User saveUser = userRepository.findOneWithAuthoritiesByEmail(user.getEmail()).get();
		assertEquals(user.getEmail(), saveUser.getEmail());
	}

	@Test
	@DisplayName("회원 저장_실패(중복회원)")
	void saveUser_fail_duplicateUser() {
		// given
		Authority authority = Authority.builder()
			.authorityName("ROLE_USER")
			.build();

		User user = User.builder()
			.email("user@miraeit.net")
			.username("user")
			.password(passwordEncoder.encode("user"))
			.phone("01021234122")
			.activated(true)
			.authorities(Collections.singleton(authority)) // 추후 로직 확인
			.build();

		// when
		assertThrows(DataIntegrityViolationException.class, () -> {
			saveUser_transactional(user);
		});

		// then
	}

	@Commit
	void saveUser_transactional(User user) {
		userRepository.createUser(user);
	}


	@Test
	@DisplayName("회원조회 by 이메일")
	void findUserByEmail() {
		// given
		String admin = "admin@miraeit.net";

		// when
		User findAdmin = userRepository.findOneByEmail(admin).get();

		// then
		assertEquals(admin, findAdmin.getEmail());
	}

	@Test
	@DisplayName("회원조회 by 이메일_실패")
	void findUserByEmail_fail() {
		// given
		String unknown = "unknown@miraeit.net";

		// when
		assertThrows(NoSuchElementException.class, () -> {
			userRepository.findOneByEmail(unknown).get();
		});

		// then
	}
}