package me.aco.marketplace_spring_ca.domain.repositories;

import me.aco.marketplace_spring_ca.domain.entities.ItemType;

import java.util.List;
import java.util.Optional;

/**
 * Domain repository interface (port)
 * Implementation will be in infrastructure layer
 */
public interface ItemTypeRepository {
    
    ItemType save(ItemType itemType);
    
    Optional<ItemType> findById(Long id);
    
    List<ItemType> findAll();
    
    List<ItemType> findByNameContaining(String name);
    
    void deleteById(Long id);
    
    boolean existsById(Long id);
}
