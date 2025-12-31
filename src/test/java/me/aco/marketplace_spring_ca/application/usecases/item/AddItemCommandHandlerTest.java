package me.aco.marketplace_spring_ca.application.usecases.item;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import me.aco.marketplace_spring_ca.application.dto.ItemDto;
import me.aco.marketplace_spring_ca.application.exceptions.ResourceNotFoundException;
import me.aco.marketplace_spring_ca.application.usecases.item.command.AddItemCommand;
import me.aco.marketplace_spring_ca.application.usecases.item.command.AddItemCommandHandler;
import me.aco.marketplace_spring_ca.domain.entities.Item;
import me.aco.marketplace_spring_ca.domain.entities.ItemType;
import me.aco.marketplace_spring_ca.domain.entities.User;
import me.aco.marketplace_spring_ca.domain.enums.UserRole;
import me.aco.marketplace_spring_ca.infrastructure.persistence.JpaItemRepository;
import me.aco.marketplace_spring_ca.infrastructure.persistence.JpaItemTypeRepository;
import me.aco.marketplace_spring_ca.infrastructure.persistence.JpaUserRepository;

@ExtendWith(MockitoExtension.class)
public class AddItemCommandHandlerTest {

    @Mock
    private JpaItemRepository jpaItemRepository;
    @Mock
    private JpaItemTypeRepository jpaItemTypeRepository;
    @Mock
    private JpaUserRepository   jpaUserRepository;

    @InjectMocks
    private AddItemCommandHandler addItemCommandHandler;

    private AddItemCommand validAddItemCommand;
    private ItemType mockItemType;
    private User mockUser;
    private Item mockItem;

    @BeforeEach
    void setUp() {

        validAddItemCommand = new AddItemCommand(
            "Test Item",
            "Test Description",
            99.99,
            1L,
            1L
        );

        mockItemType = new ItemType(
            1L,
            "Electronics",
            "Electronic items",
            null,
            java.time.LocalDateTime.now(),
            java.time.LocalDateTime.now()
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
    void shouldAddItemSuccessfully() {

        // Arrange
        when(jpaItemTypeRepository.findById(any())).thenReturn(Optional.of(mockItemType));
        when(jpaUserRepository.findById(any())).thenReturn(Optional.of(mockUser));
        when(jpaItemRepository.save(any())).thenReturn(mockItem);

        // Act
        var result = addItemCommandHandler.handle(validAddItemCommand);

        // Assert
        assertNotNull(result);
    }

    @Test
    void shouldThrowExceptionIfItemTypeNotFound() {

        // Arrange
        var command = new AddItemCommand(
            "Test Item",
            "Test Description",
            99.99,
            1L,
            1L
        );

        when(jpaItemTypeRepository.findById(any())).thenReturn(Optional.empty());

        // Act
        // Act & Assert
        Exception exception = assertThrows(ResourceNotFoundException.class, () -> addItemCommandHandler.handle(command));
        assertTrue(exception.getMessage().toLowerCase().contains("item type not found"));
    }

    @Test
    void shouldThrowExceptionIfUserNotFound() {

        // Arrange
        var command = new AddItemCommand(
            "Test Item",
            "Test Description",
            99.99,
            1L,
            1L
        );

        var itemType = new ItemType(
            1L,
            "Electronics",
            "Electronic items",
            null,
            java.time.LocalDateTime.now(),
            java.time.LocalDateTime.now()
        );

        when(jpaItemTypeRepository.findById(any())).thenReturn(Optional.of(itemType));
        when(jpaUserRepository.findById(any())).thenReturn(Optional.empty());

        // Act
        // Act & Assert
        Exception exception = assertThrows(ResourceNotFoundException.class, () -> addItemCommandHandler.handle(command));
        assertTrue(exception.getMessage().toLowerCase().contains("user not found"));
    }

    @Test
    void shouldThrowExceptionIfSaveFails() {

        // Arrange
        var command = new AddItemCommand(
            "Test Item",
            "Test Description",
            99.99,
            1L,
            1L
        );

        var itemType = new ItemType(
            1L,
            "Electronics",
            "Electronic items",
            null,
            java.time.LocalDateTime.now(),
            java.time.LocalDateTime.now()
        );

        var user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setPassword("password");
        user.setName("Test User");
        user.setEmail("test@example.com");
        user.setBalance(BigDecimal.valueOf(1000));
        user.setRole(UserRole.USER);

        when(jpaItemTypeRepository.findById(any())).thenReturn(Optional.of(itemType));
        when(jpaUserRepository.findById(any())).thenReturn(Optional.of(user));
        when(jpaItemRepository.save(any())).thenThrow(new RuntimeException("Save failed"));

        // Act
        // Act & Assert
        Exception exception = assertThrows(RuntimeException.class, () -> addItemCommandHandler.handle(command));
        assertTrue(exception.getMessage().toLowerCase().contains("save failed"));
    }
}
