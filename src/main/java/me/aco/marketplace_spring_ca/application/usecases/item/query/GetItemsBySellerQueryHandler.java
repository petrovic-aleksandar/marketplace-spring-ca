package me.aco.marketplace_spring_ca.application.usecases.item.query;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import me.aco.marketplace_spring_ca.application.dto.ItemDto;
import me.aco.marketplace_spring_ca.application.exceptions.ResourceNotFoundException;
import me.aco.marketplace_spring_ca.infrastructure.persistence.JpaItemRepository;
import me.aco.marketplace_spring_ca.infrastructure.persistence.JpaUserRepository;

public class GetItemsBySellerQueryHandler {

    private final JpaItemRepository itemRepository;
    private final JpaUserRepository userRepository;

    public GetItemsBySellerQueryHandler(JpaItemRepository itemRepository, JpaUserRepository userRepository) {
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
    }

    public CompletableFuture<List<ItemDto>> handle(GetItemsBySellerQuery query) {
        return CompletableFuture.supplyAsync(() -> 
            itemRepository.findBySeller(userRepository.findById(query.sellerId())
                    .orElseThrow(()-> new ResourceNotFoundException("Seller not found")))
                    .stream()
                    .map(ItemDto::new)
                    .toList());
    }
    
}
