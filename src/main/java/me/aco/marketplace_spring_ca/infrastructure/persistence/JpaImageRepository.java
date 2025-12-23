package me.aco.marketplace_spring_ca.infrastructure.persistence;

import me.aco.marketplace_spring_ca.domain.entities.Image;
import me.aco.marketplace_spring_ca.domain.entities.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface JpaImageRepository extends JpaRepository<Image, Long> {
    List<Image> findByItemId(Long itemId);
    Optional<Image> findFrontImageByItemId(Long itemId);
    Optional<Image> findByItemAndFrontTrue(Item item);
}
