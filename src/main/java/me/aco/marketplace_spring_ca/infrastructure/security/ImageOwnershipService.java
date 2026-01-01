package me.aco.marketplace_spring_ca.infrastructure.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import me.aco.marketplace_spring_ca.domain.entities.Image;
import me.aco.marketplace_spring_ca.infrastructure.persistence.JpaImageRepository;
import me.aco.marketplace_spring_ca.infrastructure.persistence.JpaItemRepository;
import me.aco.marketplace_spring_ca.infrastructure.persistence.JpaUserRepository;

@Service
@RequiredArgsConstructor
public class ImageOwnershipService {

    private final JpaImageRepository imageRepository;
    private final JpaItemRepository itemRepository;
    private final JpaUserRepository userRepository;

    /**
     * Checks if the given user owns the image (through item ownership).
     * Only the owner of the item that the image belongs to can modify the image.
     * 
     * @param imageId the ID of the image
     * @param userDetails the authenticated user
     * @return true if the user owns the item that the image belongs to, false otherwise
     */
    public boolean isImageOwner(Long imageId, UserDetails userDetails) {
        if (imageId == null || userDetails == null) {
            return false;
        }

        Image image = imageRepository.findById(imageId).orElse(null);
        if (image == null) {
            return false;
        }

        var user = userRepository.findSingleByUsername(userDetails.getUsername()).orElse(null);
        if (user == null) {
            return false;
        }

        // User must own the item that the image belongs to
        return image.getItem().getSeller().getId().equals(user.getId());
    }

    /**
     * Checks if the given user owns the item.
     * Used for validating item ownership before adding images.
     * 
     * @param itemId the ID of the item
     * @param userDetails the authenticated user
     * @return true if the user owns the item, false otherwise
     */
    public boolean isItemOwner(Long itemId, UserDetails userDetails) {
        if (itemId == null || userDetails == null) {
            return false;
        }

        var item = itemRepository.findById(itemId).orElse(null);
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
