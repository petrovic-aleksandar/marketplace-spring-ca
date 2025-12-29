package me.aco.marketplace_spring_ca.application.dto;

import java.time.format.DateTimeFormatter;

import me.aco.marketplace_spring_ca.domain.entities.Item;
import me.aco.marketplace_spring_ca.domain.entities.ItemType;

public record ItemDto(
    Long id,
        String name,
        String description,
        double price,
        ItemType type,
        boolean active,
        boolean deleted,
        String createdAt,
        UserDto seller,
        ImageDto frontImage
) {
    private static final DateTimeFormatter ISO_INSTANT_NO_MILLIS = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");

    public ItemDto(Item item) {
        this(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getPrice().doubleValue(),
                item.getType(),
                item.isActive(),
                false,
                item.getCreatedAt() != null ? item.getCreatedAt().format(ISO_INSTANT_NO_MILLIS) : "",
                new UserDto(item.getSeller()),
                null
        );
    }

    public ItemDto(Item item, ImageDto frontImage) {
        this(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getPrice().doubleValue(),
                item.getType(),
                item.isActive(),
                false,
                item.getCreatedAt() != null ? item.getCreatedAt().format(ISO_INSTANT_NO_MILLIS) : "",
                new UserDto(item.getSeller()),
                frontImage
        );
    }
}
