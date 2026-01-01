package me.aco.marketplace_spring_ca.infrastructure.security;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import me.aco.marketplace_spring_ca.infrastructure.persistence.JpaUserRepository;

@Service
@RequiredArgsConstructor
public class TransferOwnershipService {

    private final JpaUserRepository userRepository;

    /**
     * Checks if the authenticated user matches the specified user ID
     * @param userId the user ID to validate
     * @param userDetails the authenticated user
     * @return true if the authenticated user's ID matches, false otherwise
     */
    public boolean isUserIdOwner(Long userId, org.springframework.security.core.userdetails.UserDetails userDetails) {
        if (userId == null || userDetails == null) {
            return false;
        }

        var user = userRepository.findSingleByUsername(userDetails.getUsername()).orElse(null);
        if (user == null) {
            return false;
        }

        return user.getId().equals(userId);
    }
}
