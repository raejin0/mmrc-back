package net.miraeit.mmrc.domain.user.repository;

import lombok.RequiredArgsConstructor;
import net.miraeit.mmrc.domain.user.entity.User;

import javax.persistence.EntityManager;

@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepositoryCustom {

	private final EntityManager em;

	@Override
	public void createUser(User user) {
		em.persist(user);
		em.flush();
		em.clear();
	}
}