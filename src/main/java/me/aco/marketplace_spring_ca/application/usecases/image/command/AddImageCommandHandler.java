package me.aco.marketplace_spring_ca.application.usecases.image.command;

import java.util.concurrent.CompletableFuture;


import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import me.aco.marketplace_spring_ca.application.dto.ImageDto;
import me.aco.marketplace_spring_ca.application.exceptions.ResourceNotFoundException;
import me.aco.marketplace_spring_ca.domain.intefrace.FileStorageService;
import me.aco.marketplace_spring_ca.infrastructure.persistence.JpaImageRepository;
import me.aco.marketplace_spring_ca.infrastructure.persistence.JpaItemRepository;

@Service
@Transactional
public class AddImageCommandHandler {

    private final JpaImageRepository imageRepository;
    private final JpaItemRepository itemRepository;
    private final FileStorageService fileStorageService;
    
    public AddImageCommandHandler(JpaImageRepository imageRepository, JpaItemRepository itemRepository, FileStorageService fileStorageService) {
        this.imageRepository = imageRepository;
        this.itemRepository = itemRepository;
        this.fileStorageService = fileStorageService;
    }

    public CompletableFuture<ImageDto> handle(AddImageCommand command) {
        return CompletableFuture.supplyAsync(() -> {
            // Save the file to storage
            fileStorageService.saveToFile(command.fileStream(), command.fileName());

            var item = itemRepository.findById(command.itemId())
                .orElseThrow(() -> new ResourceNotFoundException("Item not found"));
            
            // Create and save the image entity
            var image = new me.aco.marketplace_spring_ca.domain.entities.Image();
            image.setPath(command.fileName());
            image.setItem(item);
            image.setFront(false);
            var savedImage = imageRepository.save(image);
            
            // Return the ImageDto
            return new ImageDto(savedImage);
        });
    }

}