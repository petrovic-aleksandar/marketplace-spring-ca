package me.aco.marketplace_spring_ca.domain.repositories;

import me.aco.marketplace_spring_ca.domain.entities.Product;
import me.aco.marketplace_spring_ca.domain.entities.User;

import java.util.List;
import java.util.Optional;

/**
 * Domain repository interface (port)
 * Implementation will be in infrastructure layer
 */
public interface ProductRepository {
    
    Product save(Product product);
    
    Optional<Product> findById(Long id);
    
    List<Product> findAll();
    
    List<Product> findBySeller(User seller);
    
    List<Product> findByNameContaining(String name);
    
    void deleteById(Long id);
    
    boolean existsById(Long id);
}
