package me.aco.marketplace_spring_ca.application.usecases.auth.command;

import java.util.concurrent.CompletableFuture;

import org.springframework.stereotype.Service;

import me.aco.marketplace_spring_ca.domain.entities.User;
import me.aco.marketplace_spring_ca.domain.intefrace.RefreshTokenService;
import me.aco.marketplace_spring_ca.infrastructure.persistence.JpaUserRepository;

@Service
public class RefreshTokenCommandHandler {

    private JpaUserRepository userRepository;
    private RefreshTokenService refreshTokenService;

    public RefreshTokenCommandHandler(JpaUserRepository userRepository,
            RefreshTokenService refreshTokenService) {
        this.userRepository = userRepository;
        this.refreshTokenService = refreshTokenService;
    }

    public CompletableFuture<Long> handle(RefreshTokenCommand command) {
        // if access token is valid but expired, generate new refresh token
        return CompletableFuture.supplyAsync(() -> {
            var user = userRepository.findById(command.userId())
                    .orElseThrow(() -> new IllegalArgumentException("User not found"));
            
            user.setRefreshToken(refreshTokenService.generateRefreshToken());
            User savedUser = userRepository.save(user);

            return savedUser.getId();
        });
    }
    
}
