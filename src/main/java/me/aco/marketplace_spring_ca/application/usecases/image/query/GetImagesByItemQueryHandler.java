package me.aco.marketplace_spring_ca.application.usecases.image.query;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import me.aco.marketplace_spring_ca.application.dto.ImageDto;
import me.aco.marketplace_spring_ca.application.usecases.util.CollectionUtils;
import me.aco.marketplace_spring_ca.infrastructure.persistence.CrudImageRepository;

@Service
@Transactional(readOnly = true)
public class GetImagesByItemQueryHandler {

    private CrudImageRepository imageRepository;

    public GetImagesByItemQueryHandler(CrudImageRepository imageRepository) {
        this.imageRepository = imageRepository;
    }

    public CompletableFuture<List<ImageDto>> handle(GetImagesByItemQuery query) {
        return CompletableFuture.supplyAsync(() -> imageRepository.findByItemId(query.itemId()))
                .thenApply(images -> CollectionUtils.streamOf(images)
                        .map(ImageDto::new)
                        .toList());
    }
    
}
