package me.aco.marketplace_spring_ca.infrastructure.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import me.aco.marketplace_spring_ca.domain.entities.Item;
import me.aco.marketplace_spring_ca.infrastructure.persistence.JpaItemRepository;
import me.aco.marketplace_spring_ca.infrastructure.persistence.JpaUserRepository;

@Service
@RequiredArgsConstructor
public class ItemOwnershipService {

    private final JpaItemRepository itemRepository;
    private final JpaUserRepository userRepository;

    /**
     * Checks if the given user owns the item with the specified ID
     * @param itemId the ID of the item
     * @param userDetails the authenticated user
     * @return true if the user owns the item, false otherwise
     */
    public boolean isItemOwner(Long itemId, UserDetails userDetails) {
        if (itemId == null || userDetails == null) {
            return false;
        }

        Item item = itemRepository.findById(itemId).orElse(null);
        if (item == null) {
            return false;
        }

        var user = userRepository.findSingleByUsername(userDetails.getUsername()).orElse(null);
        if (user == null) {
            return false;
        }

        return item.getSeller().getId().equals(user.getId());
    }
}
