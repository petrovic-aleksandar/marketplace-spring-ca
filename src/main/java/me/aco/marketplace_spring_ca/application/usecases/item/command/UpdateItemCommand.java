package me.aco.marketplace_spring_ca.application.usecases.item.command;

import java.math.BigDecimal;

public record UpdateItemCommand(
        Long id,
        String name,
        String description,
        BigDecimal price,
        Long typeId,
        Long sellerId
) {
        public static UpdateItemCommand withId(long id, UpdateItemCommand command) {
            return new UpdateItemCommand(
                    id,
                    command.name(),
                    command.description(),
                    command.price(),
                    command.typeId(),
                    command.sellerId()
            );
        }
}
