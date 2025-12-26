package me.aco.marketplace_spring_ca.application.usecases.image.command;

import java.io.InputStream;

public record AddImageCommand(
    Long itemId, 
    String fileName, 
    InputStream fileStream
) {
    public static AddImageCommand withStream(InputStream stream, AddImageCommand command) {
        return new AddImageCommand(command.itemId(), command.fileName(), stream);
    }

    public static AddImageCommand withId(Long itemId, AddImageCommand command) {
        return new AddImageCommand(itemId, command.fileName(), command.fileStream());
    }
}
