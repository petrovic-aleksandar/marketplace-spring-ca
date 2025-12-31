package me.aco.marketplace_spring_ca.application.usecases.item.command;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import me.aco.marketplace_spring_ca.application.dto.ItemDto;
import me.aco.marketplace_spring_ca.application.exceptions.ResourceNotFoundException;
import me.aco.marketplace_spring_ca.domain.entities.Item;
import me.aco.marketplace_spring_ca.domain.entities.ItemType;
import me.aco.marketplace_spring_ca.domain.entities.User;
import me.aco.marketplace_spring_ca.infrastructure.persistence.JpaItemRepository;
import me.aco.marketplace_spring_ca.infrastructure.persistence.JpaItemTypeRepository;
import me.aco.marketplace_spring_ca.infrastructure.persistence.JpaUserRepository;

@Service
@Transactional
@RequiredArgsConstructor
public class UpdateItemCommandHandler {

    private final JpaItemRepository itemRepository;
    private final JpaItemTypeRepository itemTypeRepository;
    private final JpaUserRepository userRepository;

    public ItemDto handle(UpdateItemCommand command) {

        var item = fetchItem(command.id());
        var itemType = fetchItemType(command.typeId());
        var seller = fetchSeller(command.sellerId());
        
        item = updateItem(item, command, itemType, seller);
        item = itemRepository.save(item);
        return new ItemDto(item);  
    }

    private Item fetchItem(long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Item not found"));
    }

    private Item updateItem(Item item, UpdateItemCommand command, ItemType itemType, User seller) {
        item.setName(command.name());
        item.setDescription(command.description());
        item.setPrice(BigDecimal.valueOf(command.price()));
        item.setType(itemType);
        item.setSeller(seller);
        item.setUpdatedAt(LocalDateTime.now());
        return item;
    }

    private ItemType fetchItemType(long typeId) {
        return itemTypeRepository.findById(typeId)
                .orElseThrow(() -> new ResourceNotFoundException("Item type not found"));
    }

    private User fetchSeller(long sellerId) {
        return userRepository.findById(sellerId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }
    
}
