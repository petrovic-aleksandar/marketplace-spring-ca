package me.aco.marketplace_spring_ca.infrastructure.persistence;

import me.aco.marketplace_spring_ca.domain.entities.ItemType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JpaItemTypeRepository extends JpaRepository<ItemType, Long> {
    List<ItemType> findByNameContainingIgnoreCase(String name);
}
