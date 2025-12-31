package me.aco.marketplace_spring_ca.application.usecases.item;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import me.aco.marketplace_spring_ca.application.dto.ItemDto;
import me.aco.marketplace_spring_ca.application.exceptions.BusinessException;
import me.aco.marketplace_spring_ca.application.exceptions.ResourceNotFoundException;
import me.aco.marketplace_spring_ca.application.usecases.item.command.ActivateItemCommand;
import me.aco.marketplace_spring_ca.domain.entities.Item;
import me.aco.marketplace_spring_ca.domain.entities.ItemType;
import me.aco.marketplace_spring_ca.domain.entities.User;
import me.aco.marketplace_spring_ca.domain.enums.UserRole;
import me.aco.marketplace_spring_ca.infrastructure.persistence.JpaItemRepository;

@ExtendWith(MockitoExtension.class)
public class ActivateItemCommandHandlerTest {

    @Mock
    private JpaItemRepository jpaItemRepository;

    @InjectMocks
    private me.aco.marketplace_spring_ca.application.usecases.item.command.ActivateItemCommandHandler activateItemCommandHandler;

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
        mockItem.setActive(false);
        mockItem.setDeleted(false);
    }

    @Test
    void shouldActivateItemSuccessfully() {
        // Arrange
        when(jpaItemRepository.findById(any())).thenReturn(Optional.of(mockItem));
        when(jpaItemRepository.save(any(Item.class))).thenAnswer(invocation -> {
            Item item = invocation.getArgument(0);
            item.setActive(true);
            return item;
        });

        ActivateItemCommand command = new ActivateItemCommand(1L);

        // Act
        ItemDto result = activateItemCommandHandler.handle(command);

        // Assert
        assertNotNull(result);
        assertTrue(mockItem.isActive());
    }

    @Test
    void shouldThrowExceptionIfItemNotFound() {
        // Arrange
        when(jpaItemRepository.findById(any())).thenReturn(Optional.empty());

        // Act & Assert
        ActivateItemCommand command = new ActivateItemCommand(99L);
        assertThrows(ResourceNotFoundException.class, () -> activateItemCommandHandler.handle(command));
    }

    @Test
    void shouldThrowExceptionIfItemAlreadyActive() {
        // Arrange
        mockItem.setActive(true);
        when(jpaItemRepository.findById(any())).thenReturn(Optional.of(mockItem));

        // Act & Assert
        ActivateItemCommand command = new ActivateItemCommand(1L);
        Exception exception = assertThrows(BusinessException.class, () -> activateItemCommandHandler.handle(command));
        assertTrue(exception.getMessage().toLowerCase().contains("already active"));
    }
}
