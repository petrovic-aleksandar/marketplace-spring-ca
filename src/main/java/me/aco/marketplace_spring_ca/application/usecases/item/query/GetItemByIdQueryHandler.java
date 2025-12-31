package me.aco.marketplace_spring_ca.application.usecases.item.query;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import me.aco.marketplace_spring_ca.application.dto.ItemDto;
import me.aco.marketplace_spring_ca.application.exceptions.ResourceNotFoundException;
import me.aco.marketplace_spring_ca.infrastructure.persistence.JpaItemRepository;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GetItemByIdQueryHandler {

    private final JpaItemRepository itemRepository;

    public ItemDto handle(GetItemByIdQuery query) {
        validateQuery(query);

        return itemRepository.findByIdWithImages(query.id())
                .map(ItemDto::new)
                .orElseThrow(() -> new ResourceNotFoundException("Item not found"));
    }

    private void validateQuery(GetItemByIdQuery query) {
        if (query.id() == null)
            throw new IllegalArgumentException("Item ID cannot be null");
    }

}
