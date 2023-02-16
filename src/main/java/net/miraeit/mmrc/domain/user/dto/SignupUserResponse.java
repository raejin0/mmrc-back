package net.miraeit.mmrc.domain.user.dto;

import com.sun.istack.NotNull;
import lombok.*;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SignupUserResponse {

	@NotNull
	private String email;

	@NotNull
	private String username;

	private String phone;

	private boolean activated;
}
