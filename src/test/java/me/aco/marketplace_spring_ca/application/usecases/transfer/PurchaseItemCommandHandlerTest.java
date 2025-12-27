package me.aco.marketplace_spring_ca.application.usecases.transfer;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.concurrent.CompletionException;

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
import me.aco.marketplace_spring_ca.domain.entities.ItemType;
import me.aco.marketplace_spring_ca.domain.entities.User;
import me.aco.marketplace_spring_ca.domain.entities.transfers.PurchaseTransfer;
import me.aco.marketplace_spring_ca.domain.enums.UserRole;
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

    private PurchaseItemCommand purchaseItemCommand;

    private User buyer;
    private User seller;
    private Item item;

    @BeforeEach
    void setUp() {
        buyer = new User(
            1L,
            "buyerUsername",
            "hashedPassword",
            "Buyer Name",
            "buyer@email.com",
            "1234567890",
            new BigDecimal("200.00"),
            UserRole.USER,
            true,
            null,
            null,
            java.time.LocalDateTime.now()
        );

        seller = new User(
            2L,
            "sellerUsername",
            "hashedPassword",
            "Seller Name",
            "seller@email.com",
            "0987654321",
            new BigDecimal("500.00"),
            UserRole.USER,
            true,
            null,
            null,
            LocalDateTime.now()
        );

        ItemType itemType = new ItemType(
            1L,
            "Electronics",
            "Electronic items",
            null,
            LocalDateTime.now(),
            LocalDateTime.now()
        );
        
        item = new Item(
            1L,
            "Item Title",
            "Item Description",
            new BigDecimal("100.00"),
            itemType,
            true,
            false,
            seller,
            null,
            LocalDateTime.now()
        );
    }

    @Test
    void handle_shouldReturnTransferDto_whenPurchaseIsValid() throws Exception {

        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(buyer));
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        purchaseItemCommand = new PurchaseItemCommand(1L, 1L);

        // Act
        TransferDto result = purchaseItemCommandHandler.handle(purchaseItemCommand).get();

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
        purchaseItemCommand = new PurchaseItemCommand(1L, 1L);

        // Act & Assert
        CompletionException thrown = assertThrows(
            CompletionException.class,
            () -> purchaseItemCommandHandler.handle(purchaseItemCommand).join()
        );
        assertNotNull(thrown.getCause());
        assertThrows(ResourceNotFoundException.class, () -> { throw thrown.getCause(); });
    }

    @Test
    void handle_shouldThrowResourceNotFoundException_whenItemNotFound() {

        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(buyer));
        when(itemRepository.findById(1L)).thenReturn(Optional.empty());
        purchaseItemCommand = new PurchaseItemCommand(1L, 1L);

        // Act & Assert
        CompletionException thrown = assertThrows(
            CompletionException.class,
            () -> purchaseItemCommandHandler.handle(purchaseItemCommand).join()
        );
        assertNotNull(thrown.getCause());
        assertThrows(ResourceNotFoundException.class, () -> { throw thrown.getCause(); });
    }

    @Test
    void handle_shouldThrowBusinessException_whenItemNotActive() {

        // Arrange
        item.setActive(false);
        when(userRepository.findById(1L)).thenReturn(Optional.of(buyer));
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        purchaseItemCommand = new PurchaseItemCommand(1L, 1L);

        // Act & Assert
        CompletionException thrown = assertThrows(
            CompletionException.class,
            () -> purchaseItemCommandHandler.handle(purchaseItemCommand).join()
        );
        assertNotNull(thrown.getCause());
        assertThrows(BusinessException.class, () -> { throw thrown.getCause(); });
    }

    @Test
    void handle_shouldThrowBusinessException_whenItemDeleted() {

        // Arrange
        item.setDeleted(true);
        when(userRepository.findById(1L)).thenReturn(Optional.of(buyer));
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        purchaseItemCommand = new PurchaseItemCommand(1L, 1L);

        // Act & Assert
        CompletionException thrown = assertThrows(
            CompletionException.class,
            () -> purchaseItemCommandHandler.handle(purchaseItemCommand).join()
        );
        assertNotNull(thrown.getCause());
        assertThrows(BusinessException.class, () -> { throw thrown.getCause(); });
    }

    @Test
    void handle_shouldThrowBusinessException_whenInsufficientBalance() {

        // Arrange
        buyer.setBalance(new BigDecimal("99.99"));
        when(userRepository.findById(1L)).thenReturn(Optional.of(buyer));
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        purchaseItemCommand = new PurchaseItemCommand(1L, 1L);

        // Act & Assert
        CompletionException thrown = assertThrows(
            CompletionException.class,
            () -> purchaseItemCommandHandler.handle(purchaseItemCommand).join()
        );
        assertNotNull(thrown.getCause());
        assertThrows(BusinessException.class, () -> { throw thrown.getCause(); });
    }

    
    
}
