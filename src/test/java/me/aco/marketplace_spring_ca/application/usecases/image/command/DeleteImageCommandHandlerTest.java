package me.aco.marketplace_spring_ca.application.usecases.image.command;

import static org.mockito.Mockito.verify;

import java.util.concurrent.CompletableFuture;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import me.aco.marketplace_spring_ca.infrastructure.persistence.JpaImageRepository;

@ExtendWith(MockitoExtension.class)
class DeleteImageCommandHandlerTest {

    @Mock
    private JpaImageRepository imageRepository;

    @InjectMocks
    private DeleteImageCommandHandler handler;

    @Test
    void handle_deletesImageById() throws Exception {

        // Arrange
        long imageId = 42L;
        DeleteImageCommand command = new DeleteImageCommand(imageId);

        // Act
        CompletableFuture<Void> future = handler.handle(command);
        future.get();

        // Assert
        verify(imageRepository).deleteById(imageId);
    }
}
