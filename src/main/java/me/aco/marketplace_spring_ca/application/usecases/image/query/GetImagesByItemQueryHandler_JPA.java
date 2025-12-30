package me.aco.marketplace_spring_ca.application.usecases.image.query;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import me.aco.marketplace_spring_ca.application.dto.ImageDto;
import me.aco.marketplace_spring_ca.infrastructure.persistence.JpaImageRepository;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GetImagesByItemQueryHandler_JPA {

    private final JpaImageRepository imageRepository;

    public List<ImageDto> handle(GetImagesByItemQuery query) {
        return imageRepository.findByItemId(query.itemId()).stream()
                .map(ImageDto::new)
                .toList();
    }
    
}
