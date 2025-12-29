package me.aco.marketplace_spring_ca.application.usecases.transfer.command;

import java.math.BigDecimal;
import java.util.concurrent.CompletableFuture;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import me.aco.marketplace_spring_ca.application.dto.TransferDto;
import me.aco.marketplace_spring_ca.application.exceptions.ResourceNotFoundException;
import me.aco.marketplace_spring_ca.domain.entities.User;
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

            validateCommand(command);

            var user = fetchUser(command.userId());
            
            PaymentTransfer transfer = new PaymentTransfer();
            transfer.setUser(user);
            transfer.setAmount(command.amount());

            user.addBalance(command.amount());

            transferRepository.save(transfer);
            userRepository.save(user);

            return new TransferDto(transfer);
        });
    }

    private void validateCommand(AddPaymentCommand command) {
        if (command.amount().compareTo(BigDecimal.ZERO) <= 0)
            throw new IllegalArgumentException("Amount must be greater than zero");
    }

    private User fetchUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }
}
