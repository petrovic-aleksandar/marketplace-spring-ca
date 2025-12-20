package me.aco.marketplace_spring_ca.application.usecases.auth.command;

public record RefreshTokenCommand(
    Long userId, 
    String accessToken, 
    String refreshToken) {
    
}
