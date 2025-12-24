package me.aco.marketplace_spring_ca.application.usecases.transfer.command;

public record AddWithdrawalCommand(
    long userId, 
    double amount
) {}
