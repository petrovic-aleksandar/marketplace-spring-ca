package me.aco.marketplace_spring_ca.application.usecases.image.query;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import me.aco.marketplace_spring_ca.application.dto.ImageDto;
import me.aco.marketplace_spring_ca.application.usecases.util.CollectionUtils;
import me.aco.marketplace_spring_ca.infrastructure.persistence.CrudImageRepository;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GetImagesByItemQueryHandler {

    private final CrudImageRepository imageRepository;

    public List<ImageDto> handle(GetImagesByItemQuery query) {
        return CollectionUtils.streamOf(imageRepository.findByItemId(query.itemId()))
                .map(ImageDto::new)
                .toList();
    }
    
}
