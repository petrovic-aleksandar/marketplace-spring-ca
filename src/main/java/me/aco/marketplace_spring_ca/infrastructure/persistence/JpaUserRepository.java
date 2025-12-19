package me.aco.marketplace_spring_ca.infrastructure.persistence;

import me.aco.marketplace_spring_ca.domain.entities.User;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaUserRepository extends JpaRepository<User, Long> {
    Optional<User> findSingleByUsername(String username);
}
