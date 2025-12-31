package me.aco.marketplace_spring_ca.application.usecases.item.query;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import me.aco.marketplace_spring_ca.application.dto.ItemDto;
import me.aco.marketplace_spring_ca.application.exceptions.ResourceNotFoundException;
import me.aco.marketplace_spring_ca.infrastructure.persistence.JpaItemRepository;
import me.aco.marketplace_spring_ca.infrastructure.persistence.JpaUserRepository;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GetItemsBySellerQueryHandler {

    private final JpaItemRepository itemRepository;
    private final JpaUserRepository userRepository;

    public List<ItemDto> handle(GetItemsBySellerQuery query) {
        return itemRepository.findBySeller(userRepository.findById(query.sellerId())
                .orElseThrow(() -> new ResourceNotFoundException("Seller not found")))
                .stream()
                .map(ItemDto::new)
                .toList();
    }

}
