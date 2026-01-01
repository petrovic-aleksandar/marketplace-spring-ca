package me.aco.marketplace_spring_ca.infrastructure.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import me.aco.marketplace_spring_ca.infrastructure.persistence.JpaUserRepository;

@Service
@RequiredArgsConstructor
public class SelfOwnershipService {

    private final JpaUserRepository userRepository;

    /**
     * Checks if the authenticated user owns the profile they're trying to update
     * @param userId the ID of the user profile being updated
     * @param userDetails the authenticated user
     * @return true if the user is updating their own profile, false otherwise
     */
    public boolean isOwnProfile(Long userId, UserDetails userDetails) {
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
