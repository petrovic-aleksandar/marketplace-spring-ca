package me.aco.marketplace_spring_ca.application.usecases.item.command;

import java.util.concurrent.CompletableFuture;


import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import me.aco.marketplace_spring_ca.application.dto.ItemDto;
import me.aco.marketplace_spring_ca.application.exceptions.BusinessException;
import me.aco.marketplace_spring_ca.application.exceptions.ResourceNotFoundException;
import me.aco.marketplace_spring_ca.infrastructure.persistence.JpaItemRepository;

@Service
@Transactional
public class DeactivateItemCommandHandler {

    private final JpaItemRepository itemRepository;

    public DeactivateItemCommandHandler(JpaItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    public CompletableFuture<ItemDto> handle(DeactivateItemCommand command) {
        return CompletableFuture.supplyAsync(() -> {
            var item = itemRepository.findById(command.id())
                    .orElseThrow(() -> new ResourceNotFoundException("Item not found"));
            if (!item.isActive())
                throw new BusinessException("Item is already inactive");
            item.deactivate();
            return new ItemDto(itemRepository.save(item));
        });
    }
    
}
