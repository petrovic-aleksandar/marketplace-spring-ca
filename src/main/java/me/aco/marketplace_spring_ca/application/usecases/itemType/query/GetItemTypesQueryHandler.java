package me.aco.marketplace_spring_ca.application.usecases.itemType.query;

import java.util.List;
import java.util.concurrent.CompletableFuture;


import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import me.aco.marketplace_spring_ca.application.dto.ItemTypeDto;
import me.aco.marketplace_spring_ca.infrastructure.persistence.JpaItemTypeRepository;

@Service
@Transactional(readOnly = true)
public class GetItemTypesQueryHandler {

    private final JpaItemTypeRepository itemTypeRepository;

    public GetItemTypesQueryHandler(JpaItemTypeRepository itemTypeRepository) {
        this.itemTypeRepository = itemTypeRepository;
    }

    public CompletableFuture<List<ItemTypeDto>> handle(GetItemTypesQuery query) {
        return CompletableFuture.supplyAsync(() -> itemTypeRepository.findAll())
                .thenApply(itemTypes -> itemTypes.stream()
                        .map(ItemTypeDto::new)
                        .toList());
    }
    
}
