package me.aco.marketplace_spring_ca.application.usecases.auth.command;

public record LoginCommand(
    String username, 
    String password) {
    
}
