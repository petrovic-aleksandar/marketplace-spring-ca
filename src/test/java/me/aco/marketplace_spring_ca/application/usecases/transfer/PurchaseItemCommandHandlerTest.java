package me.aco.marketplace_spring_ca.application.usecases.transfer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import me.aco.marketplace_spring_ca.application.usecases.transfer.command.PurchaseItemCommand;
import me.aco.marketplace_spring_ca.application.usecases.transfer.command.PurchaseItemCommandHandler;
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

    
    
}
