package me.aco.marketplace_spring_ca.application.usecases.item.query;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.springframework.stereotype.Service;

import me.aco.marketplace_spring_ca.application.dto.ItemDto;
import me.aco.marketplace_spring_ca.application.exceptions.ResourceNotFoundException;
import me.aco.marketplace_spring_ca.infrastructure.persistence.JpaItemRepository;
import me.aco.marketplace_spring_ca.infrastructure.persistence.JpaItemTypeRepository;

@Service
public class GetItemsByItemTypeQueryHandler {

    private final JpaItemRepository itemRepository;
    private final JpaItemTypeRepository itemTypeRepository;

    public GetItemsByItemTypeQueryHandler(JpaItemRepository itemRepository, JpaItemTypeRepository itemTypeRepository) {
        this.itemRepository = itemRepository;
        this.itemTypeRepository = itemTypeRepository;
    }

    public CompletableFuture<List<ItemDto>> handle(GetItemsByItemTypeQuery query) {
        return CompletableFuture.supplyAsync(() -> 
            itemRepository.findByType(itemTypeRepository.findById(query.typeId())
                    .orElseThrow(() -> new ResourceNotFoundException("Item type not found")))
                    .stream()
                    .map(ItemDto::new)
                    .toList());
    }
    
}
