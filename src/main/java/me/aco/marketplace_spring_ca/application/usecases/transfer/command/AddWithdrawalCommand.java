package me.aco.marketplace_spring_ca.application.usecases.transfer.command;

public record AddWithdrawalCommand(
    Long userId, 
    double amount
) {}
