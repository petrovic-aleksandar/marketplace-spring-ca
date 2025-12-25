package me.aco.marketplace_spring_ca.infrastructure.persistence;

import me.aco.marketplace_spring_ca.domain.entities.Image;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CrudImageRepository extends CrudRepository<Image, Long> {
    Iterable<Image> findByItemId(Long itemId);
}
