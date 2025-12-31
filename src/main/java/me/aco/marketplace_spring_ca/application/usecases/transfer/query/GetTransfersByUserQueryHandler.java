package me.aco.marketplace_spring_ca.application.usecases.transfer.query;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import me.aco.marketplace_spring_ca.application.dto.TransferDto;
import me.aco.marketplace_spring_ca.domain.entities.User;
import me.aco.marketplace_spring_ca.domain.entities.transfers.Transfer;
import me.aco.marketplace_spring_ca.infrastructure.persistence.JpaTransferRepository;
import me.aco.marketplace_spring_ca.infrastructure.persistence.JpaUserRepository;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GetTransfersByUserQueryHandler {

    private final JpaUserRepository userRepository;
    private final JpaTransferRepository transferRepository;

    public List<TransferDto> handle(GetTransfersByUserQuery query) {

        validateQuery(query);

        User user = fetchUser(query.userId());

        return fetchTransfers(user).stream()
                .map(TransferDto::new)
                .toList();
    }

    private void validateQuery(GetTransfersByUserQuery query) {
        if (query.userId() == null)
            throw new IllegalArgumentException("User ID cannot be null");
    }

    private User fetchUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    private List<Transfer> fetchTransfers(User user) {
        List<Transfer> transfers = transferRepository.findByBuyer(user);
        transfers.addAll(transferRepository.findBySeller(user));
        transfers.addAll(transferRepository.findByUser(user));
        return transfers;
    }

}
