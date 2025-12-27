package me.aco.marketplace_spring_ca.application.usecases.transfer.command;

import java.math.BigDecimal;
import java.util.concurrent.CompletableFuture;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import me.aco.marketplace_spring_ca.application.dto.TransferDto;
import me.aco.marketplace_spring_ca.application.exceptions.ResourceNotFoundException;
import me.aco.marketplace_spring_ca.domain.entities.transfers.PaymentTransfer;
import me.aco.marketplace_spring_ca.infrastructure.persistence.JpaTransferRepository;
import me.aco.marketplace_spring_ca.infrastructure.persistence.JpaUserRepository;

@Service
@Transactional
public class AddPaymentCommandHandler {

    private final JpaTransferRepository transferRepository;
    private final JpaUserRepository userRepository;

    public AddPaymentCommandHandler(JpaTransferRepository transferRepository, JpaUserRepository userRepository) {
        this.transferRepository = transferRepository;
        this.userRepository = userRepository;
    }

    public CompletableFuture<TransferDto> handle(AddPaymentCommand command) {
        return CompletableFuture.supplyAsync(() -> {
            var user = userRepository.findById(command.userId())
                    .orElseThrow(() -> new ResourceNotFoundException("User not found"));
            
            // Process payment
            PaymentTransfer transfer = new PaymentTransfer();
            transfer.setUser(user);
            transfer.setAmount(BigDecimal.valueOf(command.amount()));

            // Update balance
            user.addBalance(BigDecimal.valueOf(command.amount()));

            // Save transfer and update user
            transferRepository.save(transfer);
            userRepository.save(user);

            return new TransferDto(transfer);
        });
    }
}
