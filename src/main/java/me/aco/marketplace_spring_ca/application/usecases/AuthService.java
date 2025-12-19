package me.aco.marketplace_spring_ca.application.usecases;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import me.aco.marketplace_spring_ca.application.dto.LoginReq;
import me.aco.marketplace_spring_ca.application.exceptions.BusinessException;
import me.aco.marketplace_spring_ca.application.exceptions.ResourceNotFoundException;
import me.aco.marketplace_spring_ca.domain.entities.User;
import me.aco.marketplace_spring_ca.infrastructure.persistence.JpaUserRepository;

@Service
@Transactional
public class AuthService {

    private static final int REFRESH_TOKEN_VALIDITY_DAYS = 7;
    
    private final JpaUserRepository userRepository;

    public AuthService(JpaUserRepository userRepository) {
        this.userRepository = userRepository;
    }

	public User authenticate(LoginReq loginRequest) {
        User user = userRepository.findByEmail(loginRequest.getUsername())
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (!validatePassword(loginRequest, user))
            throw new BusinessException("Invalid credentials");

        return user;
    }

    public boolean validatePassword(LoginReq loginRequest, User user) {
        // Simple password comparison - in production, use BCrypt or similar
        return loginRequest.getPassword().equals(user.getPassword());
    }

    public String createAndSaveRefreshToken(User user) {
        String refreshToken = generateRefreshToken();
        updateUserRefreshToken(user, refreshToken);
        return refreshToken;
    }

    private void updateUserRefreshToken(User user, String refreshToken) {
        user.setRefreshToken(refreshToken);
        user.setRefreshTokenExpiry(LocalDateTime.now().plus(REFRESH_TOKEN_VALIDITY_DAYS, ChronoUnit.DAYS));
        userRepository.save(user);
    }

    private String generateRefreshToken() {
        return UUID.randomUUID().toString().replace("-", "");
    }
}
        return RandomStringUtils.secureStrong().nextAlphanumeric(REFRESH_TOKEN_LENGTH);
    }
}
