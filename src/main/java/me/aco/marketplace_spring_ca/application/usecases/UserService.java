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
		user.setUsername(request.getUsername());
		if (request.isUpdatePassword()) {
			user.setPassword(new PasswordHasherImpl().hashPassword(request.getPassword()));
		}
		user.setName(request.getName());
		user.setEmail(request.getEmail());
		user.setPhone(request.getPhone());
		user.setRole(UserRole.valueOf(request.getRole()));
		return usersRepository.save(user);
	}

	public User toUser(UserReq request) {
		User user = new User();
		user.setUsername(request.getUsername());
		user.setPassword(new PasswordHasherImpl().hashPassword(request.getPassword()));
		user.setName(request.getName());
		user.setEmail(request.getEmail());
		user.setPhone(request.getPhone());
		user.setRole(UserRole.valueOf(request.getRole()));
		user.setActive(true);
		return user;
	}
}
