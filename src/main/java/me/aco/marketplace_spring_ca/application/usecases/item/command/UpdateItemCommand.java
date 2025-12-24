package me.aco.marketplace_spring_ca.application.usecases.item.command;

public record UpdateItemCommand(
        Long id,
        String name,
        String description,
        double price,
        long typeId,
        long sellerId
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
