package me.aco.marketplace_spring_ca.domain.repositories;

import me.aco.marketplace_spring_ca.domain.entities.transfers.Transfer;
import me.aco.marketplace_spring_ca.domain.entities.User;

import java.util.List;
import java.util.Optional;

/**
 * Domain repository interface (port)
 * Implementation will be in infrastructure layer
 */
public interface TransferRepository {
    
    Transfer save(Transfer transfer);
    
    Optional<Transfer> findById(Long id);
    
    List<Transfer> findAll();
    
    List<Transfer> findByBuyerId(Long buyerId);
    
    List<Transfer> findBySellerId(Long sellerId);
    
    List<Transfer> findByUserId(Long userId);
    
    void deleteById(Long id);
    
    boolean existsById(Long id);
}
