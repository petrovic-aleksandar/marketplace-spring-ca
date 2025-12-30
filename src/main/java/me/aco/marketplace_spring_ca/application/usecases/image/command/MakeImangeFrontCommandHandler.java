package me.aco.marketplace_spring_ca.application.usecases.image.command;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import me.aco.marketplace_spring_ca.application.dto.ImageDto;
import me.aco.marketplace_spring_ca.application.exceptions.BusinessException;
import me.aco.marketplace_spring_ca.application.exceptions.ResourceNotFoundException;
import me.aco.marketplace_spring_ca.domain.entities.Image;
import me.aco.marketplace_spring_ca.domain.entities.Item;
import me.aco.marketplace_spring_ca.infrastructure.persistence.JpaImageRepository;

@Service
@Transactional
@RequiredArgsConstructor
public class MakeImangeFrontCommandHandler {

    private final JpaImageRepository imageRepository;

    public ImageDto handle(MakeImageFrontCommand command) {
        var image = fetchImage(command.imageId());

        checkIfImageIsFront(image);

        demoteCurrentFrontImage(image.getItem());

        return new ImageDto(saveImage(image));
    }

    private Image fetchImage(Long imageId) {
        return imageRepository.findById(imageId)
                .orElseThrow(() -> new ResourceNotFoundException("Image not found"));
    }

    private void checkIfImageIsFront(Image image) {
        if (image.isFront())
            throw new BusinessException("Image is already front");
    }

    private void demoteCurrentFrontImage(Item item) {
        imageRepository.findByItemAndFrontTrue(item).ifPresent(frontImage -> {
            frontImage.setFront(false);
            imageRepository.save(frontImage);
        });
    }

    private Image saveImage(Image image) {
        image.setFront(true);
        return imageRepository.save(image);
    }

}
