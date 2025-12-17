package me.aco.marketplace_spring_ca.infrastructure.adapters;

import me.aco.marketplace_spring_ca.domain.entities.transfers.Transfer;
import me.aco.marketplace_spring_ca.domain.repositories.TransferRepository;
import me.aco.marketplace_spring_ca.infrastructure.persistence.JpaTransferRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class TransferRepositoryAdapter implements TransferRepository {

    private final JpaTransferRepository jpaTransferRepository;

    public TransferRepositoryAdapter(JpaTransferRepository jpaTransferRepository) {
        this.jpaTransferRepository = jpaTransferRepository;
    }

    @Override
    public Transfer save(Transfer transfer) {
        return jpaTransferRepository.save(transfer);
    }

    @Override
    public Optional<Transfer> findById(Long id) {
        return jpaTransferRepository.findById(id);
    }

    @Override
    public List<Transfer> findAll() {
        return jpaTransferRepository.findAll();
    }

    @Override
    public List<Transfer> findByBuyerId(Long buyerId) {
        return jpaTransferRepository.findByBuyerId(buyerId);
    }

    @Override
    public List<Transfer> findBySellerId(Long sellerId) {
        return jpaTransferRepository.findBySellerId(sellerId);
    }

    @Override
    public List<Transfer> findByUserId(Long userId) {
        return jpaTransferRepository.findByUserId(userId);
    }

    @Override
    public void deleteById(Long id) {
        jpaTransferRepository.deleteById(id);
    }

    @Override
    public boolean existsById(Long id) {
        return jpaTransferRepository.existsById(id);
    }
}
