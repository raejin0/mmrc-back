package net.miraeit.mmrc.domain.user.repository;

import net.miraeit.mmrc.domain.user.dto.SignupUserResponse;
import net.miraeit.mmrc.domain.user.dto.UserListResponse;
import net.miraeit.mmrc.domain.user.entity.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long>, UserRepositoryCustom {

	/** login */
	@EntityGraph(attributePaths = "authorities") // fetchtype = eager
	Optional<User> findOneWithAuthoritiesByEmail(String email);

	Optional<User> findOneByEmail(@Param("email") String email);

	@Query("select      u.username" +
			" from       User u" +
				" where  u.email = :email")
	Optional<String> findUsernameByEmail(@Param("email") String email);

	/** user management */
	@Query("select new net.miraeit.mmrc.domain.user.dto.UserListResponse(" +
				"u.email, u.username, u.phone, u.activated)" +
			" from User u")
	List<UserListResponse> findAllUsers();

	@Override
	void createUser(User user);

	@Query("select  new net.miraeit.mmrc.domain.user.dto.SignupUserResponse(" +
						"u.email, u.username, u.phone, u.activated)" +
			" from   User u" +
			" where u.email = :email")
	Optional<SignupUserResponse> findSignupUserByEmail(@Param("email") String email);
}
