package me.aco.marketplace_spring_ca.application.usecases.auth.command;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.CompletableFuture;

import org.springframework.stereotype.Service;

import me.aco.marketplace_spring_ca.application.dto.TokenDto;
import me.aco.marketplace_spring_ca.application.exceptions.AuthenticationException;
import me.aco.marketplace_spring_ca.domain.entities.User;
import me.aco.marketplace_spring_ca.domain.intefrace.PasswordHasher;
import me.aco.marketplace_spring_ca.domain.intefrace.RefreshTokenService;
import me.aco.marketplace_spring_ca.domain.intefrace.TokenService;
import me.aco.marketplace_spring_ca.infrastructure.persistence.JpaUserRepository;

@Service
public class LoginCommandHandler {

    private static final int REFRESH_TOKEN_VALIDITY_DAYS = 1;

    private final JpaUserRepository userRepository;
    private final RefreshTokenService refreshTokenService;
    private final TokenService tokenService;
    private final PasswordHasher passwordHasher;

    public LoginCommandHandler(RefreshTokenService refreshTokenService, TokenService tokenService, JpaUserRepository userRepository, PasswordHasher passwordHasher) {
        this.refreshTokenService = refreshTokenService;
        this.tokenService = tokenService;
        this.userRepository = userRepository;
        this.passwordHasher = passwordHasher;
    }

    public CompletableFuture<TokenDto> handle(LoginCommand command) {
        return authenticate(command)
            .thenCompose(user -> {
                String accessToken = tokenService.generateToken(user);
                String refreshToken = refreshTokenService.generateRefreshToken();
                
                user.setRefreshToken(refreshToken);
                user.setRefreshTokenExpiry(LocalDateTime.now().plus(REFRESH_TOKEN_VALIDITY_DAYS, ChronoUnit.DAYS));
                
                return CompletableFuture.supplyAsync(() -> {
                    userRepository.save(user);
                    return new TokenDto(accessToken, refreshToken);
                });
            });
    }

    private CompletableFuture<User> authenticate(LoginCommand command) {
        return CompletableFuture.supplyAsync(() -> {
            User user = userRepository.findSingleByUsername(command.username())
                .orElseThrow(() -> new AuthenticationException("Invalid credentials"));

            if (!passwordHasher.verifyPassword(command.password(), user.getPassword()))
                throw new AuthenticationException("Invalid credentials");

            return user;
        });
    }
    
}
