package me.aco.marketplace_spring_ca.application.usecases.item.command;

public record UpdateItemCommand(
        long id,
        String name,
        String description,
        double price,
        long typeId,
        long sellerId
) {}
