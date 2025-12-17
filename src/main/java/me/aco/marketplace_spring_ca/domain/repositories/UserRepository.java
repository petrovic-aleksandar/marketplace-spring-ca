package me.aco.marketplace_spring_ca.domain.repositories;

import me.aco.marketplace_spring_ca.domain.entities.User;

import java.util.Optional;

/**
 * Domain repository interface (port)
 * Implementation will be in infrastructure layer
 */
public interface UserRepository {
    
    User save(User user);
    
    Optional<User> findById(Long id);
    
    Optional<User> findByEmail(String email);
    
    boolean existsByEmail(String email);
    
    void deleteById(Long id);
}
