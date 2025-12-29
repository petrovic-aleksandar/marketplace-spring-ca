package me.aco.marketplace_spring_ca.application.usecases.transfer.command;

import java.math.BigDecimal;

public record AddWithdrawalCommand(
    Long userId, 
    BigDecimal amount
) {}
