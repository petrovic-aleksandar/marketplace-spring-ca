package me.aco.marketplace_spring_ca.application.usecases.itemType.query;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import me.aco.marketplace_spring_ca.application.dto.ItemTypeDto;
import me.aco.marketplace_spring_ca.infrastructure.persistence.JpaItemTypeRepository;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GetItemTypesQueryHandler {

    private final JpaItemTypeRepository itemTypeRepository;

    public List<ItemTypeDto> handle(GetItemTypesQuery query) {
        return itemTypeRepository.findAll().stream()
                .map(ItemTypeDto::new)
                .toList();
    }

}
