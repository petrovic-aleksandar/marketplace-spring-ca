package me.aco.marketplace_spring_ca.application.usecases.image.command;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import me.aco.marketplace_spring_ca.application.dto.ImageDto;
import me.aco.marketplace_spring_ca.application.exceptions.ResourceNotFoundException;
import me.aco.marketplace_spring_ca.domain.entities.Image;
import me.aco.marketplace_spring_ca.domain.entities.Item;
import me.aco.marketplace_spring_ca.domain.intefrace.FileStorageService;
import me.aco.marketplace_spring_ca.infrastructure.persistence.JpaImageRepository;
import me.aco.marketplace_spring_ca.infrastructure.persistence.JpaItemRepository;

@Service
@Transactional
@RequiredArgsConstructor
public class AddImageCommandHandler {

    private final JpaImageRepository imageRepository;
    private final JpaItemRepository itemRepository;
    private final FileStorageService fileStorageService;

    public ImageDto handle(AddImageCommand command) {

        var item = fetchItem(command.itemId());

        saveToFile(command);

        return new ImageDto(saveImage(command, item));
    }

    private Item fetchItem(Long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Item not found"));
    }

    private void saveToFile(AddImageCommand command) {
        fileStorageService.saveToFile(command.fileStream(), command.fileName());
    }

    private Image saveImage(AddImageCommand command, Item item) {
        var image = new me.aco.marketplace_spring_ca.domain.entities.Image();
        image.setPath(command.fileName());
        image.setItem(item);
        image.setFront(false);
        return imageRepository.save(image);
    }

}