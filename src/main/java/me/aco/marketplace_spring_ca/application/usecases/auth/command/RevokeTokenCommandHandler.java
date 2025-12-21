package me.aco.marketplace_spring_ca.application.usecases.auth.command;

import java.util.concurrent.CompletableFuture;

import org.springframework.stereotype.Service;

import me.aco.marketplace_spring_ca.domain.entities.User;
import me.aco.marketplace_spring_ca.infrastructure.persistence.JpaUserRepository;

@Service
public class RevokeTokenCommandHandler {

    private JpaUserRepository userRepository;

    public RevokeTokenCommandHandler(JpaUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public CompletableFuture<Long> handle(RevokeTokenCommand command) {
        return CompletableFuture.supplyAsync(() -> {
            var user = userRepository.findById(command.userId())
                    .orElseThrow(() -> new IllegalArgumentException("User not found"));
            
            user.setRefreshToken(null);
            User savedUser = userRepository.save(user);
            return savedUser.getId();
        });
    }
    
}
