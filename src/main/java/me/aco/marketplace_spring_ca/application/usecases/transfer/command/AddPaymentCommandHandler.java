package me.aco.marketplace_spring_ca.application.usecases.transfer.command;

import java.math.BigDecimal;
import java.util.concurrent.CompletableFuture;

import org.springframework.stereotype.Service;

import me.aco.marketplace_spring_ca.application.dto.TransferDto;
import me.aco.marketplace_spring_ca.domain.entities.transfers.PaymentTransfer;
import me.aco.marketplace_spring_ca.infrastructure.persistence.JpaTransferRepository;
import me.aco.marketplace_spring_ca.infrastructure.persistence.JpaUserRepository;

@Service
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
                    .orElseThrow(() -> new RuntimeException("User not found"));

            PaymentTransfer transfer = new PaymentTransfer();
            transfer.setUser(user);
            transfer.setAmount(BigDecimal.valueOf(command.amount()));
            user.addBalance(BigDecimal.valueOf(command.amount()));
            transferRepository.save(transfer);
            userRepository.save(user);
            return new TransferDto(transfer);
        });
    }
}
