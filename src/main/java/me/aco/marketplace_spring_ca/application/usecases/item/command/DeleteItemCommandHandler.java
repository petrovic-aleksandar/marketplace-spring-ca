package me.aco.marketplace_spring_ca.application.usecases.item.command;

import java.util.concurrent.CompletableFuture;

import me.aco.marketplace_spring_ca.application.dto.ItemDto;
import me.aco.marketplace_spring_ca.application.exceptions.ResourceNotFoundException;
import me.aco.marketplace_spring_ca.infrastructure.persistence.JpaItemRepository;

public class DeleteItemCommandHandler {

    private final JpaItemRepository itemRepository;

    public DeleteItemCommandHandler(JpaItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    public CompletableFuture<ItemDto> handle(DeleteItemCommand command) {
        return CompletableFuture.supplyAsync(() -> {
            var item = itemRepository.findById(command.id())
                    .orElseThrow(() -> new ResourceNotFoundException("Item not found"));
            item.softDelete();
            return new ItemDto(itemRepository.save(item));
        });
    }
    
}
