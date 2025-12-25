package me.aco.marketplace_spring_ca.application.usecases.item.command;

import java.util.concurrent.CompletableFuture;


import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import me.aco.marketplace_spring_ca.application.exceptions.ResourceNotFoundException;
import me.aco.marketplace_spring_ca.infrastructure.persistence.JpaItemRepository;

@Service
@Transactional
public class DeleteItemCommandHandler {

    private final JpaItemRepository itemRepository;

    public DeleteItemCommandHandler(JpaItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    public CompletableFuture<Void> handle(DeleteItemCommand command) {
        return CompletableFuture.runAsync(() -> {
            var item = itemRepository.findById(command.id())
                    .orElseThrow(() -> new ResourceNotFoundException("Item not found"));
            item.softDelete();
            itemRepository.save(item);
        });
    }
    
}
