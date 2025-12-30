package me.aco.marketplace_spring_ca.application.usecases.item;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import me.aco.marketplace_spring_ca.application.dto.ItemDto;
import me.aco.marketplace_spring_ca.application.exceptions.BusinessException;
import me.aco.marketplace_spring_ca.application.exceptions.ResourceNotFoundException;
import me.aco.marketplace_spring_ca.application.usecases.item.command.DeactivateItemCommand;
import me.aco.marketplace_spring_ca.domain.entities.Item;
import me.aco.marketplace_spring_ca.domain.entities.ItemType;
import me.aco.marketplace_spring_ca.domain.entities.User;
import me.aco.marketplace_spring_ca.domain.enums.UserRole;
import me.aco.marketplace_spring_ca.infrastructure.persistence.JpaItemRepository;

@ExtendWith(MockitoExtension.class)
public class DeactivateItemCommandHandlerTest {

    @Mock
    private JpaItemRepository jpaItemRepository;

    @InjectMocks
    private me.aco.marketplace_spring_ca.application.usecases.item.command.DeactivateItemCommandHandler deactivateItemCommandHandler;

    private Item mockItem;
    private User mockUser;
    private ItemType mockItemType;

    @BeforeEach
    void setUp() {

        mockItemType = new ItemType(
            1L, 
            "Electronics", 
            "Electronic items", 
            null, 
            LocalDateTime.now(), 
            LocalDateTime.now()
        );

        mockUser = new User();
        mockUser.setId(1L);
        mockUser.setUsername("testuser");
        mockUser.setPassword("password");
        mockUser.setName("Test User");
        mockUser.setEmail("test@example.com");
        mockUser.setBalance(BigDecimal.valueOf(1000));
        mockUser.setRole(UserRole.USER);

        mockItem = new Item();
        mockItem.setId(1L);
        mockItem.setName("Test Item");
        mockItem.setDescription("Test Description");
        mockItem.setPrice(BigDecimal.valueOf(99.99));
        mockItem.setType(mockItemType);
        mockItem.setSeller(mockUser);
        mockItem.setActive(true);
        mockItem.setDeleted(false);
    }

    @Test
    void shouldDeactivateItemSuccessfully() throws Exception {

        // Arrange
        when(jpaItemRepository.findById(any())).thenReturn(Optional.of(mockItem));
        when(jpaItemRepository.save(any(Item.class))).thenAnswer(invocation -> {
            Item item = invocation.getArgument(0);
            item.setActive(false);
            return item;
        });
        DeactivateItemCommand command = new DeactivateItemCommand(1L);

        // Act
        CompletableFuture<ItemDto> future = deactivateItemCommandHandler.handle(command);

        // Assert
        assertNotNull(future.get());
        assertFalse(mockItem.isActive());
    }

    @Test
    void shouldThrowExceptionIfItemNotFound() {

        // Arrange
        when(jpaItemRepository.findById(any())).thenReturn(Optional.empty());
        DeactivateItemCommand command = new DeactivateItemCommand(99L);

        // Act
        CompletableFuture<ItemDto> future = deactivateItemCommandHandler.handle(command);

        // Assert
        Exception exception = assertThrows(Exception.class, future::get);
        Throwable cause = exception.getCause();
        assertNotNull(cause);
        assertTrue(cause instanceof ResourceNotFoundException);
        assertTrue(exception.getMessage().toLowerCase().contains("item not found"));
    }

    @Test
    void shouldThrowExceptionIfItemAlreadyInactive() {

        // Arrange
        mockItem.setActive(false);
        when(jpaItemRepository.findById(any())).thenReturn(Optional.of(mockItem));
        DeactivateItemCommand command = new DeactivateItemCommand(1L);

        // Act
        CompletableFuture<ItemDto> future = deactivateItemCommandHandler.handle(command);

        // Assert
        Exception exception = assertThrows(Exception.class, future::get);
        Throwable cause = exception.getCause();
        assertNotNull(cause);
        assertTrue(cause instanceof BusinessException);
        assertTrue(exception.getMessage().toLowerCase().contains("already inactive"));
    }
}
