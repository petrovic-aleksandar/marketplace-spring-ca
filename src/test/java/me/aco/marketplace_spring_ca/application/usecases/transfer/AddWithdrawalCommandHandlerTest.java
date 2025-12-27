package me.aco.marketplace_spring_ca.application.usecases.transfer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
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
import me.aco.marketplace_spring_ca.application.usecases.transfer.command.AddWithdrawalCommand;
import me.aco.marketplace_spring_ca.application.usecases.transfer.command.AddWithdrawalCommandHandler;
import me.aco.marketplace_spring_ca.domain.entities.User;
import me.aco.marketplace_spring_ca.domain.enums.UserRole;
import me.aco.marketplace_spring_ca.infrastructure.persistence.JpaTransferRepository;
import me.aco.marketplace_spring_ca.infrastructure.persistence.JpaUserRepository;

@ExtendWith(MockitoExtension.class)
public class AddWithdrawalCommandHandlerTest {

    @Mock
    private JpaTransferRepository transferRepository;
    @Mock
    private JpaUserRepository userRepository;
    
    @InjectMocks
    private AddWithdrawalCommandHandler addWithdrawalCommandHandler;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User(
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
    }

    @Test
    void handle_shoudRetunTransferDto_whenCommandIsValid() throws Exception {

        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        AddWithdrawalCommand command = new AddWithdrawalCommand(1L, new BigDecimal("100.0"));

        // Act
        TransferDto result = addWithdrawalCommandHandler.handle(command).get();

        // Assert
        assertNotNull(result);
        assertEquals(new BigDecimal("100.0"), result.amount());
        verify(transferRepository).save(any());
        verify(userRepository).save(any());
    }

    @Test
    void handle_shouldThrowException_whenInsufficientBalance() {

        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        AddWithdrawalCommand command = new AddWithdrawalCommand(1L, new BigDecimal("300.0"));

        // Act & Assert
        CompletionException thrown = assertThrows(
            CompletionException.class,
            () -> addWithdrawalCommandHandler.handle(command).join()
        );
        assertNotNull(thrown.getCause());
        assertEquals(BusinessException.class, thrown.getCause().getClass());
        assertEquals("Insufficient balance", thrown.getCause().getMessage());
    }

    @Test
    void handle_shouldThrowResourceNotFoundException_whenUserNotFound() {

        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        AddWithdrawalCommand command = new AddWithdrawalCommand(1L, new BigDecimal("100.0"));

        // Act & Assert
        CompletionException thrown = assertThrows(
            CompletionException.class,
            () -> addWithdrawalCommandHandler.handle(command).join()
        );
        assertNotNull(thrown.getCause());
        assertEquals(ResourceNotFoundException.class, thrown.getCause().getClass());
    }

    @Test
    void handle_shouldThrowIllegalArgumentException_whenAmountIsNegative() {

        // Arrange
        AddWithdrawalCommand command = new AddWithdrawalCommand(1L, new BigDecimal("-50.0"));

        // Act & Assert
        CompletionException thrown = assertThrows(
            CompletionException.class,
            () -> addWithdrawalCommandHandler.handle(command).join()
        );
        assertNotNull(thrown.getCause());
        assertEquals(IllegalArgumentException.class, thrown.getCause().getClass());
    }
    
}
