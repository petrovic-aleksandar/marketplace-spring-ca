package me.aco.marketplace_spring_ca.application.usecases.transfer.command;

public record AddPaymentCommand(
    Long userId, 
    double amount
) {}
