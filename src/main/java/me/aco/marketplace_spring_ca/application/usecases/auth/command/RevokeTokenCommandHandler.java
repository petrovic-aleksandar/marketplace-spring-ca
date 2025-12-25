package me.aco.marketplace_spring_ca.application.usecases.auth.command;

import java.util.concurrent.CompletableFuture;


import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import me.aco.marketplace_spring_ca.domain.entities.User;
import me.aco.marketplace_spring_ca.infrastructure.persistence.JpaUserRepository;

@Service
@Transactional
public class RevokeTokenCommandHandler {

    private JpaUserRepository userRepository;

    public RevokeTokenCommandHandler(JpaUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public CompletableFuture<Long> handle(RevokeTokenCommand command) {
        return CompletableFuture.supplyAsync(() -> 
            userRepository.findById(command.userId())
                    .orElseThrow(() -> new IllegalArgumentException("User not found"))
        ).thenCompose(user -> {
            user.setRefreshToken(null);
            return CompletableFuture.supplyAsync(() -> {
                User savedUser = userRepository.save(user);
                return savedUser.getId();
            });
        });
    }
    
}
