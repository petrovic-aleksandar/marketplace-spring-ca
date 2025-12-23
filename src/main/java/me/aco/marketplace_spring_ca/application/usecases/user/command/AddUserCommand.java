package me.aco.marketplace_spring_ca.application.usecases.user.command;

public record AddUserCommand(
        String username,
        String password,
        String name,
        String email,
        String phone,
        String role
) {}
