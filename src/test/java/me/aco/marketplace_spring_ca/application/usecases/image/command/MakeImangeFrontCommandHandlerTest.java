package me.aco.marketplace_spring_ca.application.usecases.image.command;

import me.aco.marketplace_spring_ca.application.dto.ImageDto;
import me.aco.marketplace_spring_ca.application.exceptions.BusinessException;
import me.aco.marketplace_spring_ca.application.exceptions.ResourceNotFoundException;
import me.aco.marketplace_spring_ca.domain.entities.Image;
import me.aco.marketplace_spring_ca.domain.entities.Item;
import me.aco.marketplace_spring_ca.infrastructure.persistence.JpaImageRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MakeImangeFrontCommandHandlerTest {

    @Mock
    private JpaImageRepository imageRepository;

    @InjectMocks
    private MakeImangeFrontCommandHandler handler;

    @Test
    void handle_setsImageFront_andDemotesOldFront() throws Exception {

        // Arrange
        long imageId = 1L;
        long itemId = 2L;
        MakeImageFrontCommand command = new MakeImageFrontCommand(imageId);
        Item item = new Item();
        item.setId(itemId);
        Image image = new Image();
        image.setId(imageId);
        image.setItem(item);
        image.setFront(false);
        Image oldFront = new Image();
        oldFront.setId(3L);
        oldFront.setItem(item);
        oldFront.setFront(true);
        when(imageRepository.findById(imageId)).thenReturn(Optional.of(image));
        when(imageRepository.findFrontImageByItemId(itemId)).thenReturn(Optional.of(oldFront));
        when(imageRepository.save(any(Image.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        CompletableFuture<ImageDto> future = handler.handle(command);
        ImageDto dto = future.get();

        // Assert
        verify(imageRepository).findById(imageId);
        verify(imageRepository).findFrontImageByItemId(itemId);
        verify(imageRepository, times(2)).save(any(Image.class));
        assertTrue(dto.front());
    }

    @Test
    void handle_imageAlreadyFront_throwsBusinessException() {

        // Arrange
        long imageId = 1L;
        MakeImageFrontCommand command = new MakeImageFrontCommand(imageId);
        Image image = new Image();
        image.setId(imageId);
        image.setFront(true);
        when(imageRepository.findById(imageId)).thenReturn(Optional.of(image));

        // Act
        CompletableFuture<ImageDto> future = handler.handle(command);

        // Assert
        Exception ex = assertThrows(Exception.class, future::get);
        assertTrue(ex.getCause() instanceof BusinessException);
    }

    @Test
    void handle_imageNotFound_throwsResourceNotFoundException() {

        // Arrange
        long imageId = 1L;
        MakeImageFrontCommand command = new MakeImageFrontCommand(imageId);
        when(imageRepository.findById(imageId)).thenReturn(Optional.empty());

        // Act
        CompletableFuture<ImageDto> future = handler.handle(command);
        Exception ex = assertThrows(Exception.class, future::get);

        // Assert
        assertTrue(ex.getCause() instanceof ResourceNotFoundException);
    }
}
