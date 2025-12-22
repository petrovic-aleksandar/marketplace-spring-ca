package me.aco.marketplace_spring_ca.application.dto;

import me.aco.marketplace_spring_ca.domain.entities.User;

public record UserDto(
	long id,
	String username,
	String name,
	String email,
	String phone,
	double balance,
	String role,
	boolean active
) {
	public UserDto(User user) {
		this(
			user.getId(),
			user.getUsername(),
			user.getName(),
			user.getEmail(),
			user.getPhone(),
			user.getBalance().doubleValue(),
			user.getRole().toString(),
			user.isActive()
		);
	}
}
