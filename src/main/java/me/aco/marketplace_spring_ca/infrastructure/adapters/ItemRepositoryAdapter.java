package me.aco.marketplace_spring_ca.infrastructure.adapters;

import me.aco.marketplace_spring_ca.domain.entities.Item;
import me.aco.marketplace_spring_ca.domain.entities.ItemType;
import me.aco.marketplace_spring_ca.domain.entities.User;
import me.aco.marketplace_spring_ca.domain.repositories.ItemRepository;
import me.aco.marketplace_spring_ca.infrastructure.persistence.JpaItemRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class ItemRepositoryAdapter implements ItemRepository {

    private final JpaItemRepository jpaItemRepository;

    public ItemRepositoryAdapter(JpaItemRepository jpaItemRepository) {
        this.jpaItemRepository = jpaItemRepository;
    }

    @Override
    public Item save(Item item) {
        return jpaItemRepository.save(item);
    }

    @Override
    public Optional<Item> findById(Long id) {
        return jpaItemRepository.findById(id);
    }

    @Override
    public List<Item> findAll() {
        return jpaItemRepository.findAll();
    }

    @Override
    public List<Item> findBySeller(User seller) {
        return jpaItemRepository.findBySeller(seller);
    }

    @Override
    public List<Item> findByType(ItemType type) {
        return jpaItemRepository.findByType(type);
    }

    @Override
    public List<Item> findByNameContaining(String name) {
        return jpaItemRepository.findByNameContainingIgnoreCase(name);
    }

    @Override
    public List<Item> findByActiveTrue() {
        return jpaItemRepository.findByActiveTrue();
    }

    @Override
    public List<Item> findByDeletedFalse() {
        return jpaItemRepository.findByDeletedFalse();
    }

    @Override
    public void deleteById(Long id) {
        jpaItemRepository.deleteById(id);
    }

    @Override
    public boolean existsById(Long id) {
        return jpaItemRepository.existsById(id);
    }
}
