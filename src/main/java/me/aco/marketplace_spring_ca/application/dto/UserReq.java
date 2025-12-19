package me.aco.marketplace_spring_ca.application.dto;

import java.security.NoSuchAlgorithmException;

import me.aco.marketplace_spring_ca.domain.enums.UserRole;
import me.aco.marketplace_spring_ca.domain.entities.User;

public class UserReq {
	
	private String username;
	private boolean updatePassword;
	private String password;
	private String name;
	private String email;
	private String phone;
	private String role;
	
	public User toUser() {
		User user = new User();
		user.setUsername(username);
		// Password should be hashed by the service layer
		user.setPassword(password);
		user.setName(name);
		user.setEmail(email);
		user.setPhone(phone);
		user.setRole(UserRole.valueOf(role));
		user.setActive(true);
		return user;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public boolean isUpdatePassword() {
		return updatePassword;
	}

	public void setUpdatePassword(boolean updatePassword) {
		this.updatePassword = updatePassword;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}
}
