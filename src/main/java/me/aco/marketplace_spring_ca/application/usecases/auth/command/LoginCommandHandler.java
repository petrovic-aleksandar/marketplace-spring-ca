package me.aco.marketplace_spring_ca.application.usecases.auth.command;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.CompletableFuture;

import org.springframework.stereotype.Service;

import me.aco.marketplace_spring_ca.application.dto.TokenDto;
import me.aco.marketplace_spring_ca.application.exceptions.BusinessException;
import me.aco.marketplace_spring_ca.application.exceptions.ResourceNotFoundException;
import me.aco.marketplace_spring_ca.domain.entities.User;
import me.aco.marketplace_spring_ca.domain.intefrace.PasswordHasher;
import me.aco.marketplace_spring_ca.domain.intefrace.RefreshTokenService;
import me.aco.marketplace_spring_ca.domain.intefrace.TokenService;
import me.aco.marketplace_spring_ca.infrastructure.persistence.JpaUserRepository;

@Service
public class LoginCommandHandler {

    private static final int REFRESH_TOKEN_VALIDITY_DAYS = 7;

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
        return CompletableFuture.supplyAsync(() -> {
            User user = authenticate(command);
            TokenDto resp = new TokenDto(
                    tokenService.generateToken(user),
                    createAndSaveRefreshToken(user));
            return resp;
        });
    }

    public User authenticate(LoginCommand command) {
        User user = userRepository.findSingleByUsername(command.username())
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (!validatePassword(command, user))
            throw new BusinessException("Invalid credentials");

        return user;
    }

    public boolean validatePassword(LoginCommand command, User user) {
        return passwordHasher.verifyPassword(command.password(), user.getPassword());
    }

    public String createAndSaveRefreshToken(User user) {
        String refreshToken = refreshTokenService.generateRefreshToken();
        user.setRefreshToken(refreshToken);
        user.setRefreshTokenExpiry(LocalDateTime.now().plus(REFRESH_TOKEN_VALIDITY_DAYS, ChronoUnit.DAYS));
        userRepository.save(user);
        return refreshToken;
    }
    
}
