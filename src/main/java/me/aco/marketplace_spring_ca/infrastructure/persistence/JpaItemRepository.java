package me.aco.marketplace_spring_ca.infrastructure.persistence;

import me.aco.marketplace_spring_ca.domain.entities.Item;
import me.aco.marketplace_spring_ca.domain.entities.ItemType;
import me.aco.marketplace_spring_ca.domain.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

import java.util.Optional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

@Repository
public interface JpaItemRepository extends JpaRepository<Item, Long> {

    List<Item> findBySeller(User seller);
    
    List<Item> findByType(ItemType type);

    @Query("SELECT i FROM Item i LEFT JOIN FETCH i.images WHERE i.id = :id")
    Optional<Item> findByIdWithImages(@Param("id") Long id);
}
