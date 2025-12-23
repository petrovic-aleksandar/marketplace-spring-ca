package me.aco.marketplace_spring_ca.application.dto;

import me.aco.marketplace_spring_ca.domain.entities.ItemType;

public record ItemTypeDto(
    Long id,
    String name,
    String description,
    String imagePath
) {

    public ItemTypeDto(ItemType itemType) {
        this(
            itemType.getId(),
            itemType.getName(),
            itemType.getDescription(),
            itemType.getImagePath()
        );
    }
    
}
