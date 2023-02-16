package net.miraeit.mmrc.domain.user.entity;

import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Authority {

//	@Id @GeneratedValue
//	private String id;

//	@Id
//	@Column(name = "authority_name", length = 50, unique = true)
//	@ManyToOne
//	@JoinColumn(name = "authority_name")
//	private String authorityName;


	// --------
	@Id
	@Column(name = "authority_name", length = 50, unique = true)
//	@ManyToOne
//	@JoinColumn(name = "authority_name")
	private String authorityName;

}