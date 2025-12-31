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

        validateCommand(command);

        var itemType = fetchItemType(command.typeId());
        var seller = fetchSeller(command.sellerId());

        var item = toItem(command, itemType, seller);
        item = saveItem(item);
        return new ItemDto(item);
    }

    private void validateCommand(AddItemCommand command) {
        if (command.name() == null || command.name().isBlank())
            throw new IllegalArgumentException("Item name cannot be null or blank");

        if (command.description() == null || command.description().isBlank())
            throw new IllegalArgumentException("Item description cannot be null or blank");

        if (command.price() == null)
            throw new IllegalArgumentException("Item price cannot be null");

        if (command.price().compareTo(BigDecimal.ZERO) < 0)
            throw new IllegalArgumentException("Item price cannot be negative");

        if (command.typeId() == null)
            throw new IllegalArgumentException("Item type ID cannot be null");

        if (command.sellerId() == null)
            throw new IllegalArgumentException("Seller ID cannot be null");
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
                command.price(),
                itemType,
                seller);
    }

    private Item saveItem(Item item) {
        return itemRepository.save(item);
    }
}
