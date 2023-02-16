package net.miraeit.mmrc.domain.user.entity;

import lombok.*;
import javax.persistence.*;
import java.util.Set;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SequenceGenerator(
		name = "USER_SEQ_GENERATOR",
		sequenceName = "USER_SEQ",
		initialValue = 3,
		allocationSize = 1
)
public class User {

	@Id
	@Column(name = "user_id")
    @GeneratedValue(strategy = GenerationType.SEQUENCE,
                    generator = "USER_SEQ_GENERATOR")
	private Long id;

	@Column(unique = true)
	private String email;

	@Column(length = 50)
	private String username;

	@Column(length = 100)
	private String password;

	@Column(length = 50)
	private String phone;

	@Column
	private boolean activated;

	@ManyToMany
	@JoinTable(
			name = "user_authority",
			joinColumns = {@JoinColumn(name = "user_id", referencedColumnName = "user_id")},
			inverseJoinColumns = {@JoinColumn(name = "authority_name", referencedColumnName = "authority_name")})
	private Set<Authority> authorities;

	public User(String email, String username, String password, String phone) {
		this.email = email;
		this.username = username;
		this.password = password;
		this.phone = phone;
	}

	public User(String email, String username, String password, String phone, boolean activated) {
		this.email = email;
		this.username = username;
		this.password = password;
		this.phone = phone;
		this.activated = activated;
	}

	public User(String email, String username, String password, String phone, boolean activated, Set<Authority> authorities) {
		this.email = email;
		this.username = username;
		this.password = password;
		this.phone = phone;
		this.activated = activated;
		this.authorities = authorities;
	}
}
