package me.aco.marketplace_spring_ca.application.usecases.transfer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
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
import me.aco.marketplace_spring_ca.application.exceptions.ResourceNotFoundException;
import me.aco.marketplace_spring_ca.application.usecases.transfer.command.AddPaymentCommand;
import me.aco.marketplace_spring_ca.application.usecases.transfer.command.AddPaymentCommandHandler;
import me.aco.marketplace_spring_ca.domain.entities.User;
import me.aco.marketplace_spring_ca.domain.enums.UserRole;
import me.aco.marketplace_spring_ca.infrastructure.persistence.JpaTransferRepository;
import me.aco.marketplace_spring_ca.infrastructure.persistence.JpaUserRepository;

@ExtendWith(MockitoExtension.class)
public class AddPaymentCommandHandlerTest {

    @Mock
    private JpaTransferRepository transferRepository;
    @Mock
    private JpaUserRepository userRepository;

    @InjectMocks
    private AddPaymentCommandHandler addPaymentCommandHandler;

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
    void handle_shouldAddPaymentAndReturnTransferDto() throws Exception {

        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        AddPaymentCommand command = new AddPaymentCommand(1L, new BigDecimal("100.0"));

        // Act
        TransferDto result = addPaymentCommandHandler.handle(command);

        // Assert
        assertNotNull(result);
        verify(transferRepository).save(any());
        verify(userRepository).save(user);
        assertEquals(new BigDecimal("300.00"), user.getBalance());
    }

    @Test
    void handle_shouldThrowResourceNotFoundException_whenUserNotFound() {

        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        AddPaymentCommand command = new AddPaymentCommand(1L, new BigDecimal("100.0"));

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> addPaymentCommandHandler.handle(command));
    }

    @Test
    void handle_shouldThrowIllegalArgumentException_whenAmountIsNegative() {

        // Arrange
        AddPaymentCommand command = new AddPaymentCommand(1L, new BigDecimal("-50.0"));

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> addPaymentCommandHandler.handle(command));
    }


     
}
