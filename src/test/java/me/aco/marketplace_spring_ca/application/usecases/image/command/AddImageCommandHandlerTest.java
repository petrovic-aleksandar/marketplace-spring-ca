package me.aco.marketplace_spring_ca.application.usecases.image.command;

import me.aco.marketplace_spring_ca.application.dto.ImageDto;
import me.aco.marketplace_spring_ca.application.exceptions.ResourceNotFoundException;
import me.aco.marketplace_spring_ca.domain.entities.Image;
import me.aco.marketplace_spring_ca.domain.entities.Item;
import me.aco.marketplace_spring_ca.domain.intefrace.FileStorageService;
import me.aco.marketplace_spring_ca.infrastructure.persistence.JpaImageRepository;
import me.aco.marketplace_spring_ca.infrastructure.persistence.JpaItemRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.InputStream;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AddImageCommandHandlerTest {

    @Mock
    private JpaImageRepository imageRepository;
    @Mock
    private JpaItemRepository itemRepository;
    @Mock
    private FileStorageService fileStorageService;

    @InjectMocks
    private AddImageCommandHandler handler;

    @Test
    void handle_savesImageAndReturnsDto() throws Exception {

        // Arrange
        long itemId = 1L;
        String fileName = "test.jpg";
        InputStream fileStream = mock(InputStream.class);
        AddImageCommand command = new AddImageCommand(itemId, fileName, fileStream);
        Item item = new Item();
        item.setId(itemId);
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        Image savedImage = new Image();
        savedImage.setId(10L);
        savedImage.setPath(fileName);
        savedImage.setItem(item);
        when(imageRepository.save(any(Image.class))).thenReturn(savedImage);

        // Act
        CompletableFuture<ImageDto> future = handler.handle(command);
        ImageDto dto = future.get();

        // Assert
        verify(fileStorageService).saveToFile(fileStream, fileName);
        verify(itemRepository).findById(itemId);
        verify(imageRepository).save(any(Image.class));
        assertEquals(savedImage.getId(), dto.id());
        assertEquals(savedImage.getPath(), dto.path());
    }

    @Test
    void handle_itemNotFound_throwsException() {

        // Arrange
        long itemId = 2L;
        String fileName = "notfound.jpg";
        InputStream fileStream = mock(InputStream.class);
        AddImageCommand command = new AddImageCommand(itemId, fileName, fileStream);
        when(itemRepository.findById(itemId)).thenReturn(Optional.empty());

        // Act
        CompletableFuture<ImageDto> future = handler.handle(command);

        // Assert
        Exception ex = assertThrows(Exception.class, future::get);
        assertTrue(ex.getCause() instanceof ResourceNotFoundException);
    }
}
