package me.aco.marketplace_spring_ca.application.usecases.image.command;

import java.util.concurrent.CompletableFuture;

import org.springframework.stereotype.Service;

import me.aco.marketplace_spring_ca.application.dto.ImageDto;
import me.aco.marketplace_spring_ca.application.exceptions.BusinessException;
import me.aco.marketplace_spring_ca.application.exceptions.ResourceNotFoundException;
import me.aco.marketplace_spring_ca.infrastructure.persistence.JpaImageRepository;

@Service
public class MakeImangeFrontCommandHandler {

    private final JpaImageRepository imageRepository;

    public MakeImangeFrontCommandHandler(JpaImageRepository imageRepository) {
        this.imageRepository = imageRepository;
    }

    public CompletableFuture<ImageDto> handle(MakeImageFrontCommand command) {
        return CompletableFuture.supplyAsync(() -> {
            var image = imageRepository.findById(command.imageId())
                    .orElseThrow(() -> new ResourceNotFoundException("Image not found"));

            if (image.isFront()) {
                throw new BusinessException("Image is already front");
            }

            demoteCurrentFrontImage(image.getItem().getId());

            image.setFront(true);
            imageRepository.save(image);
            return new ImageDto(image);
        });
    }

    private void demoteCurrentFrontImage(Long itemId) {
        imageRepository.findFrontImageByItemId(itemId).ifPresent(frontImage -> {
            frontImage.setFront(false);
            imageRepository.save(frontImage);
        });
    }
    
}
