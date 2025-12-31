package me.aco.marketplace_spring_ca.application.usecases.item.command;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import me.aco.marketplace_spring_ca.application.exceptions.BusinessException;
import me.aco.marketplace_spring_ca.application.exceptions.ResourceNotFoundException;
import me.aco.marketplace_spring_ca.domain.entities.Item;
import me.aco.marketplace_spring_ca.infrastructure.persistence.JpaItemRepository;

@Service
@Transactional
@RequiredArgsConstructor
public class DeleteItemCommandHandler {

    private final JpaItemRepository itemRepository;

    public void handle(DeleteItemCommand command) {

        validateCommand(command);

        var item = fetchItem(command.id());
        checkIfItemDeleted(item);

        item.softDelete();
        item = save(item);

        return;
    }

    private void validateCommand(DeleteItemCommand command) {
        if (command.id() == null)
            throw new IllegalArgumentException("Item ID must not be null");
    }

    private Item fetchItem(long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Item not found"));
    }

    private void checkIfItemDeleted(Item item) {
        if (item.isDeleted())
            throw new BusinessException("Item is already deleted");
    }

    private Item save(Item item) {
        return itemRepository.save(item);
    }
    
}
