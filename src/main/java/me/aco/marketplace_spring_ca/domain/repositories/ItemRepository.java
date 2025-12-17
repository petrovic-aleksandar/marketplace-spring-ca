package me.aco.marketplace_spring_ca.domain.repositories;

import me.aco.marketplace_spring_ca.domain.entities.Item;
import me.aco.marketplace_spring_ca.domain.entities.ItemType;
import me.aco.marketplace_spring_ca.domain.entities.User;

import java.util.List;
import java.util.Optional;

/**
 * Domain repository interface (port)
 * Implementation will be in infrastructure layer
 */
public interface ItemRepository {
    
    Item save(Item item);
    
    Optional<Item> findById(Long id);
    
    List<Item> findAll();
    
    List<Item> findBySeller(User seller);
    
    List<Item> findByType(ItemType type);
    
    List<Item> findByNameContaining(String name);
    
    List<Item> findByActiveTrue();
    
    List<Item> findByDeletedFalse();
    
    void deleteById(Long id);
    
    boolean existsById(Long id);
}
