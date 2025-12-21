package me.aco.marketplace_spring_ca.application.usecases;

import org.springframework.stereotype.Service;

import me.aco.marketplace_spring_ca.application.dto.UserReq;
import me.aco.marketplace_spring_ca.domain.enums.UserRole;
import me.aco.marketplace_spring_ca.domain.entities.User;
import me.aco.marketplace_spring_ca.infrastructure.persistence.JpaUserRepository;
import me.aco.marketplace_spring_ca.infrastructure.security.PasswordHasherImpl;

@Service
public class UserService {

	private final JpaUserRepository usersRepository;

	public UserService(JpaUserRepository usersRepository) {
		this.usersRepository = usersRepository;
	}

	public User update(UserReq request, User user) {
		user.setUsername(request.username());
		if (request.updatePassword()) {
			user.setPassword(new PasswordHasherImpl().hashPassword(request.password()));
		}
		user.setName(request.name());
		user.setEmail(request.email());
		user.setPhone(request.phone());
		user.setRole(UserRole.valueOf(request.role()));
		return usersRepository.save(user);
	}

	public User toUser(UserReq request) {
		User user = new User();
		user.setUsername(request.username());
		user.setPassword(new PasswordHasherImpl().hashPassword(request.password()));
		user.setName(request.name());
		user.setEmail(request.email());
		user.setPhone(request.phone());
		user.setRole(UserRole.valueOf(request.role()));
		user.setActive(true);
		return user;
	}
}
