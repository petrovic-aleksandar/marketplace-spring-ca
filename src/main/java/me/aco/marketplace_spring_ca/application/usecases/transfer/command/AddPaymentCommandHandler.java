package me.aco.marketplace_spring_ca.application.usecases.transfer.command;

import java.math.BigDecimal;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import me.aco.marketplace_spring_ca.application.dto.TransferDto;
import me.aco.marketplace_spring_ca.application.exceptions.ResourceNotFoundException;
import me.aco.marketplace_spring_ca.domain.entities.User;
import me.aco.marketplace_spring_ca.domain.entities.transfers.PaymentTransfer;
import me.aco.marketplace_spring_ca.infrastructure.persistence.JpaTransferRepository;
import me.aco.marketplace_spring_ca.infrastructure.persistence.JpaUserRepository;

@Service
@Transactional
@RequiredArgsConstructor
public class AddPaymentCommandHandler {

    private final JpaTransferRepository transferRepository;
    private final JpaUserRepository userRepository;

    public TransferDto handle(AddPaymentCommand command) {

        validateCommand(command);

        var user = fetchUser(command.userId());

        PaymentTransfer transfer = new PaymentTransfer();
        transfer.setUser(user);
        transfer.setAmount(command.amount());

        user.addBalance(command.amount());

        transferRepository.save(transfer);
        userRepository.save(user);

        return new TransferDto(transfer);
    }

    private void validateCommand(AddPaymentCommand command) {
        if (command.userId() == null)
            throw new IllegalArgumentException("User ID cannot be null");

        if (command.amount() == null)
            throw new IllegalArgumentException("Amount cannot be null");

        if (command.amount().compareTo(BigDecimal.ZERO) <= 0)
            throw new IllegalArgumentException("Amount must be greater than zero");
    }

    private User fetchUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }
}
