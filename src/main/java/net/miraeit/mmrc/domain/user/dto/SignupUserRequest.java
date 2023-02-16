package net.miraeit.mmrc.domain.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SignupUserRequest {

	@NotNull
	private String email;

	@NotNull
	private String username;

	private String phone;
}