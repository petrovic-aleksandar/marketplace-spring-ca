package me.aco.marketplace_spring_ca.application.usecases.auth.command;

public record RegisterCommand(
        String username,
        String password,
        String email,
        String name,
        String phone
) {}
