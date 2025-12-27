package me.aco.marketplace_spring_ca.application.usecases.transfer.command;

import java.math.BigDecimal;
import java.util.concurrent.CompletableFuture;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import me.aco.marketplace_spring_ca.application.dto.TransferDto;
import me.aco.marketplace_spring_ca.application.exceptions.BusinessException;
import me.aco.marketplace_spring_ca.application.exceptions.ResourceNotFoundException;
import me.aco.marketplace_spring_ca.domain.entities.transfers.WithdrawalTransfer;
import me.aco.marketplace_spring_ca.infrastructure.persistence.JpaTransferRepository;
import me.aco.marketplace_spring_ca.infrastructure.persistence.JpaUserRepository;

@Service
@Transactional
public class AddWithdrawalCommandHandler {

    private final JpaTransferRepository transferRepository;
    private final JpaUserRepository userRepository;

    public AddWithdrawalCommandHandler(JpaTransferRepository transferRepository, JpaUserRepository userRepository) {
        this.transferRepository = transferRepository;
        this.userRepository = userRepository;
    }

    public CompletableFuture<TransferDto> handle(AddWithdrawalCommand command) {
        return CompletableFuture.supplyAsync(() -> {
            var user = userRepository.findById(command.userId())
                    .orElseThrow(() -> new ResourceNotFoundException("User not found"));
            if (user.getBalance().compareTo(BigDecimal.valueOf(command.amount())) < 0) {
                throw new BusinessException("Insufficient balance");
            }
            WithdrawalTransfer transfer = new WithdrawalTransfer();
            transfer.setUser(user);
            transfer.setAmount(BigDecimal.valueOf(command.amount()));
            user.deductBalance(BigDecimal.valueOf(command.amount()));
            transferRepository.save(transfer);
            userRepository.save(user);
            return new TransferDto(transfer);
        });
    }
}
