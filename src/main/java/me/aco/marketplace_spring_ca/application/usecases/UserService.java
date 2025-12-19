package me.aco.marketplace_spring_ca.application.usecases;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import me.aco.marketplace_spring_ca.application.dto.UserRegReq;
import me.aco.marketplace_spring_ca.application.dto.UserReq;
import me.aco.marketplace_spring_ca.domain.enums.UserRole;
import me.aco.marketplace_spring_ca.domain.entities.User;
import me.aco.marketplace_spring_ca.infrastructure.persistence.JpaUserRepository;

@Service
public class UserService {

	@Autowired
	private JpaUserRepository userRepository;

	public User update(UserReq request, User user) {
		user.setUsername(request.getUsername());
		if (request.isUpdatePassword()) {
			// Password should be hashed before calling this method
			user.setPassword(request.getPassword());
		}
		user.setName(request.getName());
		user.setEmail(request.getEmail());
		user.setPhone(request.getPhone());
		user.setRole(UserRole.valueOf(request.getRole()));
		return userRepository.save(user);
	}

	public User toUser(UserReq request) {
		User user = new User();
		user.setUsername(request.getUsername());
		// Password should be hashed before calling this method
		user.setPassword(request.getPassword());
		user.setName(request.getName());
		user.setEmail(request.getEmail());
		user.setPhone(request.getPhone());
		user.setRole(UserRole.valueOf(request.getRole()));
		user.setActive(true);
		return user;
	}

	public User toUser(UserRegReq request) {
		User user = new User();
		user.setUsername(request.getUsername());
		// Password should be hashed before calling this method
		user.setPassword(request.getPassword());
		user.setName(request.getName());
		user.setEmail(request.getEmail());
		user.setPhone(request.getPhone());
		user.setRole(UserRole.USER);
		user.setActive(true);
		return user;
	}
}
