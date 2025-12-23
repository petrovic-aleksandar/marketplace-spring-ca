package me.aco.marketplace_spring_ca.application.usecases.item.command;

import java.math.BigDecimal;
import java.util.concurrent.CompletableFuture;

import me.aco.marketplace_spring_ca.application.dto.ItemDto;
import me.aco.marketplace_spring_ca.application.exceptions.ResourceNotFoundException;
import me.aco.marketplace_spring_ca.domain.entities.Item;
import me.aco.marketplace_spring_ca.domain.entities.ItemType;
import me.aco.marketplace_spring_ca.domain.entities.User;
import me.aco.marketplace_spring_ca.infrastructure.persistence.JpaItemRepository;
import me.aco.marketplace_spring_ca.infrastructure.persistence.JpaItemTypeRepository;
import me.aco.marketplace_spring_ca.infrastructure.persistence.JpaUserRepository;

public class AddItemCommandHandler {

    private final JpaItemRepository itemRepository;
    private final JpaItemTypeRepository itemTypeRepository;
    private final JpaUserRepository userRepository;

    public AddItemCommandHandler(JpaItemRepository itemRepository, JpaItemTypeRepository itemTypeRepository, JpaUserRepository userRepository) {
        this.itemRepository = itemRepository;
        this.itemTypeRepository = itemTypeRepository;
        this.userRepository = userRepository;
    }

    public CompletableFuture<ItemDto> handle(AddItemCommand command) {
        return CompletableFuture.supplyAsync(() -> new ItemDto(itemRepository.save(new Item(
                command.name(),
                command.description(),
                BigDecimal.valueOf(command.price()),
                findItemType(command.typeId()),
                findSeller(command.sellerId())
        ))));
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
