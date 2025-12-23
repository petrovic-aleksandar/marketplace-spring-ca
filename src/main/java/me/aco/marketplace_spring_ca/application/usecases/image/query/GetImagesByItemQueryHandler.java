package me.aco.marketplace_spring_ca.application.usecases.image.query;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.springframework.stereotype.Service;

import me.aco.marketplace_spring_ca.application.dto.ImageDto;
import me.aco.marketplace_spring_ca.infrastructure.persistence.JpaImageRepository;

@Service
public class GetImagesByItemQueryHandler {

    private JpaImageRepository imageRepository;

    public GetImagesByItemQueryHandler(JpaImageRepository imageRepository) {
        this.imageRepository = imageRepository;
    }

    public CompletableFuture<List<ImageDto>> handle(GetImagesByItemQuery query) {
        return CompletableFuture.supplyAsync(() -> imageRepository.findByItemId(query.itemId()))
                .thenApply(images -> images.stream()
                        .map(ImageDto::new)
                        .toList());
    }
    
}
