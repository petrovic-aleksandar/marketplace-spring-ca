package me.aco.marketplace_spring_ca.application.usecases.item;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import me.aco.marketplace_spring_ca.application.dto.ItemDto;
import me.aco.marketplace_spring_ca.application.exceptions.ResourceNotFoundException;
import me.aco.marketplace_spring_ca.application.usecases.item.command.UpdateItemCommand;
import me.aco.marketplace_spring_ca.application.usecases.item.command.UpdateItemCommandHandler;
import me.aco.marketplace_spring_ca.domain.entities.Item;
import me.aco.marketplace_spring_ca.domain.entities.ItemType;
import me.aco.marketplace_spring_ca.domain.entities.User;
import me.aco.marketplace_spring_ca.domain.enums.UserRole;
import me.aco.marketplace_spring_ca.infrastructure.persistence.JpaItemRepository;
import me.aco.marketplace_spring_ca.infrastructure.persistence.JpaItemTypeRepository;
import me.aco.marketplace_spring_ca.infrastructure.persistence.JpaUserRepository;

@ExtendWith(MockitoExtension.class)
public class UpdateItemCommandHandlerTest {

    @Mock
    private JpaItemRepository jpaItemRepository;
    @Mock
    private JpaItemTypeRepository jpaItemTypeRepository;
    @Mock
    private JpaUserRepository   jpaUserRepository;

    @InjectMocks
    private UpdateItemCommandHandler updateItemCommandHandler;
    
    private UpdateItemCommand validUpdateItemCommand;
    private ItemType mockItemType;
    private User mockUser;
    private Item mockItem;

    @BeforeEach
    void setUp() {

        validUpdateItemCommand = new UpdateItemCommand(
            1L,
            "Updated Item",
            "Updated Description",
            199.99,
            2L,
            2L
        );

        mockItemType = new ItemType(
            2L, "Books", 
            "Book items", 
            null, 
            LocalDateTime.now(), 
            LocalDateTime.now()
        );
        
        mockUser = new User();
        mockUser.setId(2L);
        mockUser.setUsername("updateduser");
        mockUser.setPassword("password");
        mockUser.setName("Updated User");
        mockUser.setEmail("updated@example.com");
        mockUser.setBalance(BigDecimal.valueOf(2000));
        mockUser.setRole(UserRole.USER);

        mockItem = new Item();
        mockItem.setId(1L);
        mockItem.setName("Old Item");
        mockItem.setDescription("Old Description");
        mockItem.setPrice(BigDecimal.valueOf(99.99));
        mockItem.setType(mockItemType);
        mockItem.setSeller(mockUser);
        mockItem.setActive(true);
        mockItem.setDeleted(false);
    }

    @Test
    void shouldUpdateItemSuccessfully() throws InterruptedException, ExecutionException {

        // Arrange
        when(jpaItemRepository.findById(any())).thenReturn(Optional.of(mockItem));
        when(jpaItemTypeRepository.findById(any())).thenReturn(Optional.of(mockItemType));
        when(jpaUserRepository.findById(any())).thenReturn(Optional.of(mockUser));
        when(jpaItemRepository.save(any())).thenReturn(mockItem);

        // Act
        CompletableFuture<ItemDto> future = updateItemCommandHandler.handle(validUpdateItemCommand);

        // Assert
        assertNotNull(future);
        assertDoesNotThrow(() -> future.get());
        assertTrue(future.get().name().equals("Updated Item"));
    }

    @Test
    void shouldThrowExceptionIfItemNotFound() {

        // Arrange
        when(jpaItemRepository.findById(any())).thenReturn(Optional.empty());

        // Act
        CompletableFuture<ItemDto> future = updateItemCommandHandler.handle(validUpdateItemCommand);

        // Assert
        Exception exception = assertThrows(Exception.class, future::get);
        Throwable cause = exception.getCause();
        assertNotNull(cause);
        assertTrue(cause instanceof ResourceNotFoundException);
    }

    @Test
    void shouldThrowExceptionIfItemTypeNotFound() {

        // Arrange
        when(jpaItemRepository.findById(any())).thenReturn(Optional.of(mockItem));
        when(jpaItemTypeRepository.findById(any())).thenReturn(Optional.empty());;

        // Act
        CompletableFuture<ItemDto> future = updateItemCommandHandler.handle(validUpdateItemCommand);

        // Assert
        Exception exception = assertThrows(Exception.class, future::get);
        Throwable cause = exception.getCause();
        assertNotNull(cause);
        assertTrue(cause instanceof ResourceNotFoundException);
    }

    @Test
    void shouldThrowExceptionIfUserNotFound() {

        // Arrange
        when(jpaItemRepository.findById(any())).thenReturn(Optional.of(mockItem));
        when(jpaItemTypeRepository.findById(any())).thenReturn(Optional.of(mockItemType));
        when(jpaUserRepository.findById(any())).thenReturn(Optional.empty());

        // Act
        CompletableFuture<ItemDto> future = updateItemCommandHandler.handle(validUpdateItemCommand);

        // Assert
        Exception exception = assertThrows(Exception.class, future::get);
        Throwable cause = exception.getCause();
        assertNotNull(cause);
        assertTrue(cause instanceof ResourceNotFoundException);
    }
}
