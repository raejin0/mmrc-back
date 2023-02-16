package net.miraeit.mmrc.domain.user.service;

import net.miraeit.mmrc.domain.user.entity.Authority;
import net.miraeit.mmrc.domain.user.entity.User;
import net.miraeit.mmrc.domain.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
@Transactional
class CustomUserDetailsServiceTest {

	@InjectMocks private CustomUserDetailsService customUserDetailsService;
	@Mock private UserRepository userRepository;

	@Test
	@DisplayName("권한을 포함한 회원 조회")
	void loadUserByUsername() {
	    // given
		String adminEmail = "admin@miraeit.net";
		Set<Authority> adminAuthorities;
		adminAuthorities = new HashSet<Authority>();
		adminAuthorities.add(new Authority("ROLE_ADMIN"));
		adminAuthorities.add(new Authority("ROLE_USER"));
		User admin = new User("admin@miraeit.net", "admin", "admin", "01012341234", true, adminAuthorities);

		// when
		when(userRepository.findOneWithAuthoritiesByEmail(any())).thenReturn(Optional.of(admin));
		UserDetails findAdmin = customUserDetailsService.loadUserByUsername(adminEmail);

		// then
		assertEquals(adminEmail, findAdmin.getUsername());
	}

	@Test
	@DisplayName("권한을 포함한 회원 조회_실패")
	void loadUserByUsername_fail() {
	    // given
		String userEmail = "unknown@miraeit.net";

		// when
		when(userRepository.findOneWithAuthoritiesByEmail(any())).thenReturn(Optional.empty());
		UsernameNotFoundException e = assertThrows(UsernameNotFoundException.class, () -> {
			customUserDetailsService.loadUserByUsername(userEmail);
		});
	}
}