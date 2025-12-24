package me.aco.marketplace_spring_ca.application.dto;

import me.aco.marketplace_spring_ca.domain.entities.Image;

public record ImageDto(
    Long id,
        String path,
        boolean front
) {
    public ImageDto(Image image) {
        this(
                image.getId(),
                image.getPath(),
                image.isFront()
        );
    }
}
