package me.aco.marketplace_spring_ca.application.usecases.item.query;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import me.aco.marketplace_spring_ca.application.dto.ImageDto;
import me.aco.marketplace_spring_ca.application.dto.ItemDto;
import me.aco.marketplace_spring_ca.application.exceptions.ResourceNotFoundException;
import me.aco.marketplace_spring_ca.domain.entities.Image;
import me.aco.marketplace_spring_ca.infrastructure.persistence.JpaImageRepository;
import me.aco.marketplace_spring_ca.infrastructure.persistence.JpaItemRepository;
import me.aco.marketplace_spring_ca.infrastructure.persistence.JpaItemTypeRepository;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GetItemsByItemTypeQueryHandler {

    private final JpaItemRepository itemRepository;
    private final JpaItemTypeRepository itemTypeRepository;
    private final JpaImageRepository imageRepository;

    public List<ItemDto> handle(GetItemsByItemTypeQuery query) {
        return itemRepository.findByTypeAndActiveTrue(itemTypeRepository.findById(query.typeId())
                .orElseThrow(() -> new ResourceNotFoundException("Item type not found")))
                .stream()
                .map(item -> {
                    Optional<Image> frontImage = imageRepository.findByItemAndFrontTrue(item);
                    return new ItemDto(item, frontImage.map(ImageDto::new).orElse(null));
                })
                .toList();
    }

}
