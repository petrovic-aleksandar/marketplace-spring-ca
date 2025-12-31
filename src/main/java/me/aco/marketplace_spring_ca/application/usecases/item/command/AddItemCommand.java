package me.aco.marketplace_spring_ca.application.usecases.item.command;

import java.math.BigDecimal;

public record AddItemCommand(
        String name,
        String description,
        BigDecimal price,
        Long typeId,
        Long sellerId
) {}
