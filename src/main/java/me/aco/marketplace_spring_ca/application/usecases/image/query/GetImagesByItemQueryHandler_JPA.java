package me.aco.marketplace_spring_ca.application.usecases.image.query;

import java.util.List;
import java.util.concurrent.CompletableFuture;


import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import me.aco.marketplace_spring_ca.application.dto.ImageDto;
import me.aco.marketplace_spring_ca.infrastructure.persistence.JpaImageRepository;

@Service
@Transactional(readOnly = true)
public class GetImagesByItemQueryHandler_JPA {

    private JpaImageRepository imageRepository;

    public GetImagesByItemQueryHandler_JPA(JpaImageRepository imageRepository) {
        this.imageRepository = imageRepository;
    }

    public CompletableFuture<List<ImageDto>> handle(GetImagesByItemQuery query) {
        return CompletableFuture.supplyAsync(() -> imageRepository.findByItemId(query.itemId()))
                .thenApply(images -> images.stream()
                        .map(ImageDto::new)
                        .toList());
    }
    
}
