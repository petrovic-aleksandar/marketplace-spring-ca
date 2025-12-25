package me.aco.marketplace_spring_ca.application.usecases.item.query;

import java.util.concurrent.CompletableFuture;


import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import me.aco.marketplace_spring_ca.application.dto.ItemDto;
import me.aco.marketplace_spring_ca.application.exceptions.ResourceNotFoundException;
import me.aco.marketplace_spring_ca.infrastructure.persistence.JpaItemRepository;

@Service
@Transactional(readOnly = true)
public class GetItemByIdQueryHandler {

    private final JpaItemRepository itemRepository;

    public GetItemByIdQueryHandler(JpaItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    public CompletableFuture<ItemDto> handle(GetItemByIdQuery query) {
        return CompletableFuture.supplyAsync(() -> itemRepository.findById(query.id())
                .map(ItemDto::new)
                .orElseThrow(() -> new ResourceNotFoundException("Item not found")));
    }
    
}
