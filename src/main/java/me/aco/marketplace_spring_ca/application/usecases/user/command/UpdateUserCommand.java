package me.aco.marketplace_spring_ca.application.usecases.user.command;

public record UpdateUserCommand(
    Long id,
    String username,
    boolean updatePassword,
    String password,
    String name,
    String email,
    String phone,
    String role
) {}
