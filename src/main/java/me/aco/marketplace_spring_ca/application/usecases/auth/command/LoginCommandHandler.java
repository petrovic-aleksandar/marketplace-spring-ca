package me.aco.marketplace_spring_ca.application.usecases.auth.command;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import me.aco.marketplace_spring_ca.application.dto.TokenDto;
import me.aco.marketplace_spring_ca.application.exceptions.AuthenticationException;
import me.aco.marketplace_spring_ca.domain.entities.User;
import me.aco.marketplace_spring_ca.domain.intefrace.PasswordHasher;
import me.aco.marketplace_spring_ca.domain.intefrace.RefreshTokenService;
import me.aco.marketplace_spring_ca.domain.intefrace.TokenService;
import me.aco.marketplace_spring_ca.infrastructure.persistence.JpaUserRepository;

@Service
@Transactional
public class LoginCommandHandler {

    private static final int REFRESH_TOKEN_VALIDITY_DAYS = 7;

    private final JpaUserRepository userRepository;
    private final RefreshTokenService refreshTokenService;
    private final TokenService tokenService;
    private final PasswordHasher passwordHasher;

    public LoginCommandHandler(JpaUserRepository userRepository, RefreshTokenService refreshTokenService,
            TokenService tokenService, PasswordHasher passwordHasher) {
        this.userRepository = userRepository;
        this.refreshTokenService = refreshTokenService;
        this.tokenService = tokenService;
        this.passwordHasher = passwordHasher;
    }

    public TokenDto handle(LoginCommand command) {

        validateCredentials(command);

        User user = fetchUserByUsername(command.username());
        
        authenticate(user, command.password());

        String accessToken = tokenService.generateToken(user);
        String refreshToken = refreshTokenService.generateRefreshToken();
        saveUser(user, refreshToken);

        return new TokenDto(accessToken, refreshToken);
    }

    private void validateCredentials(LoginCommand command) {
        if (command.username() == null || command.username().isEmpty())
            throw new IllegalArgumentException("Username must be provided");

        if (command.password() == null || command.password().isEmpty())
            throw new IllegalArgumentException("Password must be provided");
    }

    private User fetchUserByUsername(String username) {
        return userRepository.findSingleByUsername(username)
                .orElseThrow(() -> new AuthenticationException("Invalid credentials"));
    }

    private void authenticate(User user, String password) {
        if (!passwordHasher.verify(password, user.getPassword()))
            throw new AuthenticationException("Invalid credentials");
    }

    private User saveUser(User user, String refreshToken) {
        user.setRefreshToken(refreshToken);
        user.setRefreshTokenExpiry(LocalDateTime.now().plusDays(REFRESH_TOKEN_VALIDITY_DAYS));
        return userRepository.save(user);
    }

}
