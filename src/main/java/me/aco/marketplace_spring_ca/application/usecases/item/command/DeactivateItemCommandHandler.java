package me.aco.marketplace_spring_ca.application.usecases.item.command;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import me.aco.marketplace_spring_ca.application.dto.ItemDto;
import me.aco.marketplace_spring_ca.application.exceptions.BusinessException;
import me.aco.marketplace_spring_ca.application.exceptions.ResourceNotFoundException;
import me.aco.marketplace_spring_ca.domain.entities.Item;
import me.aco.marketplace_spring_ca.infrastructure.persistence.JpaItemRepository;

@Service
@Transactional
@RequiredArgsConstructor
public class DeactivateItemCommandHandler {

    private final JpaItemRepository itemRepository;

    public ItemDto handle(DeactivateItemCommand command) {

        validateCommand(command);

        var item = fetchItem(command.id());

        checkIfItemInactive(item);

        item.deactivate();
        item = save(item);

        return new ItemDto(item);
    }

    private void validateCommand(DeactivateItemCommand command) {
        if (command.id() == null)
            throw new IllegalArgumentException("Item ID must not be null");
    }

    private Item fetchItem(long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Item not found"));
    }

    private void checkIfItemInactive(Item item) {
        if (!item.isActive())
            throw new BusinessException("Item is already inactive");
    }

    private Item save(Item item) {
        return itemRepository.save(item);
    }
    
}
