package me.aco.marketplace_spring_ca.application.usecases.transfer.command;

public record PurchaseItemCommand(
    Long buyerId, 
    Long itemId
) {}
