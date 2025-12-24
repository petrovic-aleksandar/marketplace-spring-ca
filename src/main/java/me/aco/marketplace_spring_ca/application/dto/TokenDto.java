package me.aco.marketplace_spring_ca.application.dto;

public record TokenDto(
	String accessToken, 
	String refreshToken) {}
