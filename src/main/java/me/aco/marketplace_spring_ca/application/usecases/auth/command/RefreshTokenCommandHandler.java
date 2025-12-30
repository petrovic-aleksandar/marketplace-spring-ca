package me.aco.marketplace_spring_ca.application.usecases.auth.command;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import me.aco.marketplace_spring_ca.application.dto.TokenDto;
import me.aco.marketplace_spring_ca.application.exceptions.BusinessException;
import me.aco.marketplace_spring_ca.application.exceptions.ResourceNotFoundException;
import me.aco.marketplace_spring_ca.domain.entities.User;
import me.aco.marketplace_spring_ca.domain.intefrace.RefreshTokenService;
import me.aco.marketplace_spring_ca.domain.intefrace.TokenService;
import me.aco.marketplace_spring_ca.infrastructure.persistence.JpaUserRepository;

@Service
@Transactional
@RequiredArgsConstructor
public class RefreshTokenCommandHandler {

    @Value("${security.refresh-token-validity-days}")
    private final int refreshTokenValidityDays;

    private final JpaUserRepository userRepository;
    private final RefreshTokenService refreshTokenService;
    private final TokenService tokenService;

    public TokenDto handle(RefreshTokenCommand command) {
        
        User user = fetchUser(command.userId());

        validateTokenIgnoringExpiration(command.accessToken());

        if (isAccessTokenExpired(command.accessToken()))
            validateRefreshToken(command.refreshToken(), user);

        String newAccessToken = tokenService.generateToken(user);
        String newRefreshToken = refreshTokenService.generateRefreshToken();

        user.setRefreshToken(newRefreshToken);
        user.setRefreshTokenExpiry(LocalDateTime.now().plusDays(refreshTokenValidityDays));
        userRepository.save(user);

        return new TokenDto(newAccessToken, newRefreshToken);
    }

    private User fetchUser(Long userId) {
        if (userId == null)
            throw new IllegalArgumentException("User ID cannot be null");

        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    private void validateTokenIgnoringExpiration(String accessToken) {
        if (accessToken == null || accessToken.isEmpty())
            throw new IllegalArgumentException("Access token cannot be null or empty");

        if (!tokenService.validateTokenIgnoringExpiration(accessToken))
            throw new BusinessException("Invalid access token");
    }

    private boolean isAccessTokenExpired(String token) {
        return tokenService.isTokenExpired(token);
    }

    private void validateRefreshToken(String refreshToken, User user) {
        if (refreshToken == null || refreshToken.isEmpty())
            throw new IllegalArgumentException("Refresh token cannot be null or empty");

        if (user.getRefreshToken() == null ||
                !user.getRefreshToken().equals(refreshToken))
            throw new BusinessException("Invalid refresh token");

        if (user.getRefreshTokenExpiry() == null ||
                user.getRefreshTokenExpiry().isBefore(LocalDateTime.now()))
            throw new BusinessException("Refresh token expired");
    }

}
