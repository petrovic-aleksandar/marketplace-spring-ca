package me.aco.marketplace_spring_ca.application.usecases.item.command;

import java.util.concurrent.CompletableFuture;

import me.aco.marketplace_spring_ca.application.dto.ItemDto;
import me.aco.marketplace_spring_ca.application.exceptions.ResourceNotFoundException;
import me.aco.marketplace_spring_ca.infrastructure.persistence.JpaItemRepository;

public class DeactivateItemCommandHandler {

    private final JpaItemRepository itemRepository;

    public DeactivateItemCommandHandler(JpaItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    public CompletableFuture<ItemDto> handle(DeactivateItemCommand command) {
        return CompletableFuture.supplyAsync(() -> {
            var item = itemRepository.findById(command.id())
                    .orElseThrow(() -> new ResourceNotFoundException("Item not found"));
            item.deactivate();
            return new ItemDto(itemRepository.save(item));
        });
    }
    
}
