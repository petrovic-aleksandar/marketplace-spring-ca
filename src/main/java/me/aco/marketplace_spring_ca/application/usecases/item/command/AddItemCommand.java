package me.aco.marketplace_spring_ca.application.usecases.item.command;

public record AddItemCommand(
        String name,
        String description,
        double price,
        long typeId,
        long sellerId
) {}
