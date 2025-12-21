package me.aco.marketplace_spring_ca.application.dto;

public record UserReq(
	String username, 
	boolean updatePassword, 
	String password, 
	String name, 
	String email, 
	String phone, 
	String role) {
		
}