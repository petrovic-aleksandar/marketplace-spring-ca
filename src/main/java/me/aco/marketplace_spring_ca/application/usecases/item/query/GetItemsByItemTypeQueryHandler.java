package me.aco.marketplace_spring_ca.application.usecases.item.query;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import me.aco.marketplace_spring_ca.application.dto.ImageDto;
import me.aco.marketplace_spring_ca.application.dto.ItemDto;
import me.aco.marketplace_spring_ca.application.exceptions.ResourceNotFoundException;
import me.aco.marketplace_spring_ca.domain.entities.Image;
import me.aco.marketplace_spring_ca.infrastructure.persistence.JpaImageRepository;
import me.aco.marketplace_spring_ca.infrastructure.persistence.JpaItemRepository;
import me.aco.marketplace_spring_ca.infrastructure.persistence.JpaItemTypeRepository;

@Service
@Transactional(readOnly = true)
public class GetItemsByItemTypeQueryHandler {

    private final JpaItemRepository itemRepository;
    private final JpaItemTypeRepository itemTypeRepository;
    private final JpaImageRepository imageRepository;

    public GetItemsByItemTypeQueryHandler(JpaItemRepository itemRepository, JpaItemTypeRepository itemTypeRepository, JpaImageRepository imageRepository) {
        this.itemRepository = itemRepository;
        this.itemTypeRepository = itemTypeRepository;
        this.imageRepository = imageRepository;
    }

    public CompletableFuture<List<ItemDto>> handle(GetItemsByItemTypeQuery query) {
        return CompletableFuture.supplyAsync(() -> 
            itemRepository.findByTypeAndActiveTrue(itemTypeRepository.findById(query.typeId())
                    .orElseThrow(() -> new ResourceNotFoundException("Item type not found")))
                    .stream()
                    .map(item -> {
                        Optional<Image> frontImage = imageRepository.findByItemAndFrontTrue(item);
                        return new ItemDto(item, frontImage.map(ImageDto::new).orElse(null));
                    })
                    .toList());
    }
    
}
