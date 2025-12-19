package me.aco.marketplace_spring_ca.infrastructure.persistence;

import me.aco.marketplace_spring_ca.domain.entities.Image;
import me.aco.marketplace_spring_ca.domain.entities.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface JpaImageRepository extends JpaRepository<Image, Long> {
    Optional<Image> findByItemAndFrontTrue(Item item);
}
