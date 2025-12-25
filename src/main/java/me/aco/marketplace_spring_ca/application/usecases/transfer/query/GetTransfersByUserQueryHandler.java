package me.aco.marketplace_spring_ca.application.usecases.transfer.query;

import java.util.List;
import java.util.concurrent.CompletableFuture;


import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import me.aco.marketplace_spring_ca.application.dto.TransferDto;
import me.aco.marketplace_spring_ca.domain.entities.transfers.Transfer;
import me.aco.marketplace_spring_ca.infrastructure.persistence.JpaTransferRepository;
import me.aco.marketplace_spring_ca.infrastructure.persistence.JpaUserRepository;

@Service
@Transactional(readOnly = true)
public class GetTransfersByUserQueryHandler {

    private final JpaUserRepository userRepository;
    private final JpaTransferRepository transferRepository;

    public GetTransfersByUserQueryHandler(JpaUserRepository userRepository, JpaTransferRepository transferRepository) {
        this.userRepository = userRepository;
        this.transferRepository = transferRepository;
    }

    public CompletableFuture<List<TransferDto>> handle(GetTransfersByUserQuery query) {
        return CompletableFuture.supplyAsync(() -> {
            var user = userRepository.findById(query.userId())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            List<Transfer> transfers = transferRepository.findByBuyerId(user.getId());
            transfers.addAll(transferRepository.findBySellerId(user.getId()));
            transfers.addAll(transferRepository.findByUserId(user.getId()));

            return transfers.stream()
                    .map(TransferDto::new)
                    .toList();
        });
    }
    
}
