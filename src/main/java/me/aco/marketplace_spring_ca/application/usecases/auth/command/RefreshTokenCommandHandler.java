package me.aco.marketplace_spring_ca.application.usecases.auth.command;

import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;

import org.springframework.stereotype.Service;

import me.aco.marketplace_spring_ca.application.dto.TokenDto;
import me.aco.marketplace_spring_ca.domain.intefrace.RefreshTokenService;
import me.aco.marketplace_spring_ca.domain.intefrace.TokenService;
import me.aco.marketplace_spring_ca.infrastructure.persistence.JpaUserRepository;

@Service
public class RefreshTokenCommandHandler {

    private JpaUserRepository userRepository;
    private RefreshTokenService refreshTokenService;
    private TokenService tokenService;

    public RefreshTokenCommandHandler(JpaUserRepository userRepository,
            RefreshTokenService refreshTokenService,
            TokenService tokenService) {
        this.userRepository = userRepository;
        this.refreshTokenService = refreshTokenService;
        this.tokenService = tokenService;
    }

    public CompletableFuture<TokenDto> handle(RefreshTokenCommand command) {
        // Validate access token (allow expired)
        if (!tokenService.validateTokenIgnoringExpiration(command.accessToken())) {
            throw new IllegalArgumentException("Invalid access token");
        }
        
        return CompletableFuture.supplyAsync(() -> 
            userRepository.findById(command.userId())
                    .orElseThrow(() -> new IllegalArgumentException("User not found"))
        ).thenCompose(user -> {
            // Validate refresh token
            if (user.getRefreshToken() == null || 
                !user.getRefreshToken().equals(command.refreshToken())) {
                throw new IllegalArgumentException("Invalid refresh token");
            }
            
            // Check if refresh token is expired
            if (user.getRefreshTokenExpiry() == null || 
                user.getRefreshTokenExpiry().isBefore(LocalDateTime.now())) {
                throw new IllegalArgumentException("Refresh token expired");
            }
            
            // Generate new tokens (rotate refresh token)
            String newAccessToken = tokenService.generateToken(user);
            String newRefreshToken = refreshTokenService.generateRefreshToken();
            
            user.setRefreshToken(newRefreshToken);
            user.setRefreshTokenExpiry(LocalDateTime.now().plusDays(1));
            
            return CompletableFuture.supplyAsync(() -> {
                userRepository.save(user);
                return new TokenDto(newAccessToken, newRefreshToken);
            });
        });
    }
    
}
