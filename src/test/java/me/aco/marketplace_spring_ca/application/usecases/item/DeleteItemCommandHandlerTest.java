package me.aco.marketplace_spring_ca.application.usecases.item;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
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

import me.aco.marketplace_spring_ca.application.exceptions.ResourceNotFoundException;
import me.aco.marketplace_spring_ca.application.usecases.item.command.DeleteItemCommand;
import me.aco.marketplace_spring_ca.domain.entities.Item;
import me.aco.marketplace_spring_ca.domain.entities.ItemType;
import me.aco.marketplace_spring_ca.domain.entities.User;
import me.aco.marketplace_spring_ca.domain.enums.UserRole;
import me.aco.marketplace_spring_ca.infrastructure.persistence.JpaItemRepository;

@ExtendWith(MockitoExtension.class)
public class DeleteItemCommandHandlerTest {

    @Mock
    private JpaItemRepository jpaItemRepository;

    @InjectMocks
    private me.aco.marketplace_spring_ca.application.usecases.item.command.DeleteItemCommandHandler deleteItemCommandHandler;

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
    void shouldDeleteItemSuccessfully() {
        //  Arrange
        when(jpaItemRepository.findById(any())).thenReturn(Optional.of(mockItem));
        when(jpaItemRepository.save(any())).thenReturn(mockItem);
        DeleteItemCommand command = new DeleteItemCommand(1L);

        // Act & Assert
        assertDoesNotThrow(() -> deleteItemCommandHandler.handle(command));
        assertTrue(mockItem.isDeleted());
    }

    @Test
    void shouldThrowExceptionIfItemNotFound() {
        // Arrange
        when(jpaItemRepository.findById(any())).thenReturn(Optional.empty());
        DeleteItemCommand command = new DeleteItemCommand(99L);

        // Act & Assert
        Exception exception = assertThrows(ResourceNotFoundException.class, () -> deleteItemCommandHandler.handle(command));
        assertTrue(exception.getMessage().toLowerCase().contains("item not found"));
    }
}
