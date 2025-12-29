package me.aco.marketplace_spring_ca.application.usecases.auth.command;


import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import me.aco.marketplace_spring_ca.application.exceptions.ResourceNotFoundException;
import me.aco.marketplace_spring_ca.domain.entities.User;
import me.aco.marketplace_spring_ca.infrastructure.persistence.JpaUserRepository;

@Service
@Transactional
@RequiredArgsConstructor
public class RevokeTokenCommandHandler {

    private final JpaUserRepository userRepository;

    public Long handle(RevokeTokenCommand command) {
        User user = fetchUser(command.userId());
        return nullifyRefreshToken(user).getId();
    }

    private User fetchUser(Long userId) {
        if (userId == null)
            throw new IllegalArgumentException("User ID cannot be null");
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    private User nullifyRefreshToken(User user) {
        user.setRefreshToken(null);
        user.setRefreshTokenExpiry(null);
        return userRepository.save(user);
    }

}
