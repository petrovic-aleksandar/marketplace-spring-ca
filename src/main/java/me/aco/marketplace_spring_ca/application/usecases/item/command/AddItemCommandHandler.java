package me.aco.marketplace_spring_ca.application.usecases.item.command;

import java.math.BigDecimal;

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
public class AddItemCommandHandler {

    private final JpaItemRepository itemRepository;
    private final JpaItemTypeRepository itemTypeRepository;
    private final JpaUserRepository userRepository;

    public ItemDto handle(AddItemCommand command) {

        var itemType = fetchItemType(command.typeId());
        var seller = fetchSeller(command.sellerId());
        
        var item = toItem(command, itemType, seller);
        item = saveItem(item);
        return new ItemDto(item);
    }

    private ItemType fetchItemType(long typeId) {
        return itemTypeRepository.findById(typeId)
                .orElseThrow(() -> new ResourceNotFoundException("Item type not found"));
    }

    private User fetchSeller(long sellerId) {
        return userRepository.findById(sellerId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    private Item toItem(AddItemCommand command, ItemType itemType, User seller) {
        return new Item(
                command.name(),
                command.description(),
                BigDecimal.valueOf(command.price()),
                itemType,
                seller);
    }

    private Item saveItem(Item item) {
        return itemRepository.save(item);
    }
}
