package me.aco.marketplace_spring_ca.application.usecases.item.command;

import java.util.concurrent.CompletableFuture;

import me.aco.marketplace_spring_ca.application.dto.ItemDto;
import me.aco.marketplace_spring_ca.application.exceptions.ResourceNotFoundException;
import me.aco.marketplace_spring_ca.infrastructure.persistence.JpaItemRepository;

public class ActivateItemCommandHandler {

    private final JpaItemRepository itemRepository;

    public ActivateItemCommandHandler(JpaItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    public CompletableFuture<ItemDto> handle(ActivateItemCommand command) {
        return CompletableFuture.supplyAsync(() -> {
            var item = itemRepository.findById(command.id())
                    .orElseThrow(() -> new ResourceNotFoundException("Item not found"));
            item.activate();
            return new ItemDto(itemRepository.save(item));
        });
    }
    
}
