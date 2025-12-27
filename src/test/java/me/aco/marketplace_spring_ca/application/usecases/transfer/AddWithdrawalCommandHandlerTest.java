package me.aco.marketplace_spring_ca.application.usecases.transfer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import me.aco.marketplace_spring_ca.application.usecases.transfer.command.AddWithdrawalCommand;
import me.aco.marketplace_spring_ca.application.usecases.transfer.command.AddWithdrawalCommandHandler;
import me.aco.marketplace_spring_ca.infrastructure.persistence.JpaTransferRepository;
import me.aco.marketplace_spring_ca.infrastructure.persistence.JpaUserRepository;

@ExtendWith(MockitoExtension.class)
public class AddWithdrawalCommandHandlerTest {

    @Mock
    private JpaTransferRepository transferRepository;
    @Mock
    private JpaUserRepository userRepository;
    
    @InjectMocks
    private AddWithdrawalCommandHandler handler;

    private AddWithdrawalCommand validAddWithdrawalCommand;

    @BeforeEach
    void setUp() {

        validAddWithdrawalCommand = new AddWithdrawalCommand(
            1L,
            100.0
        );
        
    }
    
}
