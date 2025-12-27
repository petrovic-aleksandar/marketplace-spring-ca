package me.aco.marketplace_spring_ca.application.usecases.transfer;

import org.aspectj.lang.annotation.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import me.aco.marketplace_spring_ca.application.usecases.transfer.command.AddPaymentCommand;
import me.aco.marketplace_spring_ca.application.usecases.transfer.command.AddPaymentCommandHandler;
import me.aco.marketplace_spring_ca.infrastructure.persistence.JpaTransferRepository;
import me.aco.marketplace_spring_ca.infrastructure.persistence.JpaUserRepository;

@ExtendWith(MockitoExtension.class)
public class AddPaymentCommandHandlerTest {

    @Mock
    private JpaTransferRepository transferRepository;
    @Mock
    private JpaUserRepository userRepository;

    @InjectMocks
    private AddPaymentCommandHandler handler;

    private AddPaymentCommand validAddPaymentCommand;

    @BeforeEach
    void setUp() {
        
        validAddPaymentCommand = new AddPaymentCommand(
            1L,
            100.0
        );
    }

    @Test
    void testHandleValidCommand() {
        // Implement test logic here
    }

     
}
