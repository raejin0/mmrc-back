package net.miraeit.mmrc.domain.user.dto;

import com.sun.istack.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserListResponse {

	@NotNull
	private String email;

	@NotNull
	private String username;

	private String phone;

	private boolean activated;
}
