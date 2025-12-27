package me.aco.marketplace_spring_ca.application.usecases.transfer;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import me.aco.marketplace_spring_ca.application.dto.TransferDto;
import me.aco.marketplace_spring_ca.application.exceptions.BusinessException;
import me.aco.marketplace_spring_ca.application.exceptions.ResourceNotFoundException;
import me.aco.marketplace_spring_ca.application.usecases.transfer.command.PurchaseItemCommand;
import me.aco.marketplace_spring_ca.application.usecases.transfer.command.PurchaseItemCommandHandler;
import me.aco.marketplace_spring_ca.domain.entities.Item;
import me.aco.marketplace_spring_ca.domain.entities.User;
import me.aco.marketplace_spring_ca.domain.entities.transfers.PurchaseTransfer;
import me.aco.marketplace_spring_ca.infrastructure.persistence.JpaItemRepository;
import me.aco.marketplace_spring_ca.infrastructure.persistence.JpaTransferRepository;
import me.aco.marketplace_spring_ca.infrastructure.persistence.JpaUserRepository;

@ExtendWith(MockitoExtension.class)
public class PurchaseItemCommandHandlerTest {

    @Mock
    private JpaTransferRepository transferRepository;
    @Mock
    private JpaUserRepository userRepository;
    @Mock
    private JpaItemRepository itemRepository;

    @InjectMocks
    private PurchaseItemCommandHandler purchaseItemCommandHandler;

    private PurchaseItemCommand validPurchaseItemCommand;

    @BeforeEach
    void setUp() {

        validPurchaseItemCommand = new PurchaseItemCommand(
            1L,
            2L
        );
        
    }

    @Test
    void handle_shouldReturnTransferDto_whenPurchaseIsValid() throws Exception {

        // Arrange
        User buyer = mock(User.class);
        User seller = mock(User.class);
        Item item = mock(Item.class);

        when(userRepository.findById(1L)).thenReturn(Optional.of(buyer));
        when(itemRepository.findById(2L)).thenReturn(Optional.of(item));
        when(item.getSeller()).thenReturn(seller);
        when(item.getPrice()).thenReturn(BigDecimal.TEN);
        when(item.isActive()).thenReturn(true);
        when(item.isDeleted()).thenReturn(false);
        when(buyer.getBalance()).thenReturn(BigDecimal.valueOf(100));

        // Act
        TransferDto result = purchaseItemCommandHandler.handle(validPurchaseItemCommand).get();

        // Assert
        assertNotNull(result);
        verify(transferRepository).save(any(PurchaseTransfer.class));
        verify(userRepository).save(buyer);
        verify(userRepository).save(seller);
        verify(itemRepository).save(item);
    }

    @Test
    void handle_shouldThrowResourceNotFoundException_whenBuyerNotFound() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(
            me.aco.marketplace_spring_ca.application.exceptions.ResourceNotFoundException.class,
            () -> purchaseItemCommandHandler.handle(validPurchaseItemCommand).join()
        );
    }

    @Test
    void handle_shouldThrowResourceNotFoundException_whenItemNotFound() {
        // Arrange
        User buyer = mock(User.class);
        when(userRepository.findById(1L)).thenReturn(Optional.of(buyer));
        when(itemRepository.findById(2L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(
            ResourceNotFoundException.class,
            () -> purchaseItemCommandHandler.handle(validPurchaseItemCommand).join()
        );
    }

    @Test
    void handle_shouldThrowBusinessException_whenItemNotActive() {
        // Arrange
        User buyer = mock(User.class);
        Item item = mock(Item.class);
        when(userRepository.findById(1L)).thenReturn(Optional.of(buyer));
        when(itemRepository.findById(2L)).thenReturn(Optional.of(item));
        when(item.isActive()).thenReturn(false);
        when(item.isDeleted()).thenReturn(false);
        when(buyer.getBalance()).thenReturn(BigDecimal.valueOf(100));
        when(item.getPrice()).thenReturn(BigDecimal.TEN);

        // Act & Assert
        assertThrows(
            BusinessException.class,
            () -> purchaseItemCommandHandler.handle(validPurchaseItemCommand).join()
        );
    }

    @Test
    void handle_shouldThrowBusinessException_whenItemDeleted() {
        // Arrange
        User buyer = mock(User.class);
        Item item = mock(Item.class);
        when(userRepository.findById(1L)).thenReturn(Optional.of(buyer));
        when(itemRepository.findById(2L)).thenReturn(Optional.of(item));
        when(item.isActive()).thenReturn(true);
        when(item.isDeleted()).thenReturn(true);
        when(buyer.getBalance()).thenReturn(BigDecimal.valueOf(100));
        when(item.getPrice()).thenReturn(BigDecimal.TEN);

        // Act & Assert
        assertThrows(
            BusinessException.class,
            () -> purchaseItemCommandHandler.handle(validPurchaseItemCommand).join()
        );
    }

    @Test
    void handle_shouldThrowBusinessException_whenInsufficientBalance() {
        // Arrange
        User buyer = mock(User.class);
        Item item = mock(Item.class);
        when(userRepository.findById(1L)).thenReturn(Optional.of(buyer));
        when(itemRepository.findById(2L)).thenReturn(Optional.of(item));
        when(item.isActive()).thenReturn(true);
        when(item.isDeleted()).thenReturn(false);
        when(buyer.getBalance()).thenReturn(BigDecimal.valueOf(5));
        when(item.getPrice()).thenReturn(BigDecimal.TEN);

        // Act & Assert
        assertThrows(
            BusinessException.class,
            () -> purchaseItemCommandHandler.handle(validPurchaseItemCommand).join()
        );
    }

    
    
}
