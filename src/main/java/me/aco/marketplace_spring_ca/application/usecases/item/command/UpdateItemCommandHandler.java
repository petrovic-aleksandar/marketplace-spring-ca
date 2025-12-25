package me.aco.marketplace_spring_ca.application.usecases.item.command;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;


import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import me.aco.marketplace_spring_ca.application.dto.ItemDto;
import me.aco.marketplace_spring_ca.application.exceptions.ResourceNotFoundException;
import me.aco.marketplace_spring_ca.domain.entities.ItemType;
import me.aco.marketplace_spring_ca.domain.entities.User;
import me.aco.marketplace_spring_ca.infrastructure.persistence.JpaItemRepository;
import me.aco.marketplace_spring_ca.infrastructure.persistence.JpaItemTypeRepository;
import me.aco.marketplace_spring_ca.infrastructure.persistence.JpaUserRepository;

@Service
@Transactional
public class UpdateItemCommandHandler {

    private final JpaItemRepository itemRepository;
    private final JpaItemTypeRepository itemTypeRepository;
    private final JpaUserRepository userRepository;

    public UpdateItemCommandHandler(JpaItemRepository itemRepository, JpaItemTypeRepository itemTypeRepository, JpaUserRepository userRepository) {
        this.itemRepository = itemRepository;
        this.itemTypeRepository = itemTypeRepository;
        this.userRepository = userRepository;
    }

    public CompletableFuture<ItemDto> handle(UpdateItemCommand command) {
        return CompletableFuture.supplyAsync(() -> {
            var item = itemRepository.findById(command.id())
                    .orElseThrow(() -> new ResourceNotFoundException("Item not found"));
            
            item.setName(command.name());
            item.setDescription(command.description());
            item.setPrice(BigDecimal.valueOf(command.price()));
            item.setType(findItemType(command.typeId()));
            item.setSeller(findSeller(command.sellerId()));
            item.setUpdatedAt(LocalDateTime.now());
            
            return new ItemDto(itemRepository.save(item));
        });
    }

    private ItemType findItemType(long typeId) {
        return itemTypeRepository.findById(typeId)
                .orElseThrow(() -> new ResourceNotFoundException("Item type not found"));
    }

    private User findSeller(long sellerId) {
        return userRepository.findById(sellerId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }
    
}
