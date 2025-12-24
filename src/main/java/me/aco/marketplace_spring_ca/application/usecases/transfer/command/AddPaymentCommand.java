package me.aco.marketplace_spring_ca.application.usecases.transfer.command;

public record AddPaymentCommand(
    long userId, 
    double amount
) {}
