package me.aco.marketplace_spring_ca.application.dto;

import me.aco.marketplace_spring_ca.domain.entities.User;

public class UserResp {
	
	private long id;
	private String username;
	private String name;
	private String email;
	private String phone;
	private double balance;
	private String role;
	private boolean active;
	
	public UserResp(User user) {
		super();
		this.id = user.getId();
		this.username = user.getUsername();
		this.name = user.getName();
		this.email = user.getEmail();
		this.phone = user.getPhone();
		this.balance = user.getBalance().doubleValue();
		this.role = user.getRole().toString();
		this.active = user.isActive();
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
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

	public double getBalance() {
		return balance;
	}

	public void setBalance(double balance) {
		this.balance = balance;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}
}
