package me.aco.marketplace_spring_ca.infrastructure.adapters;

import me.aco.marketplace_spring_ca.domain.entities.ItemType;
import me.aco.marketplace_spring_ca.domain.repositories.ItemTypeRepository;
import me.aco.marketplace_spring_ca.infrastructure.persistence.JpaItemTypeRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class ItemTypeRepositoryAdapter implements ItemTypeRepository {

    private final JpaItemTypeRepository jpaItemTypeRepository;

    public ItemTypeRepositoryAdapter(JpaItemTypeRepository jpaItemTypeRepository) {
        this.jpaItemTypeRepository = jpaItemTypeRepository;
    }

    @Override
    public ItemType save(ItemType itemType) {
        return jpaItemTypeRepository.save(itemType);
    }

    @Override
    public Optional<ItemType> findById(Long id) {
        return jpaItemTypeRepository.findById(id);
    }

    @Override
    public List<ItemType> findAll() {
        return jpaItemTypeRepository.findAll();
    }

    @Override
    public List<ItemType> findByNameContaining(String name) {
        return jpaItemTypeRepository.findByNameContainingIgnoreCase(name);
    }

    @Override
    public void deleteById(Long id) {
        jpaItemTypeRepository.deleteById(id);
    }

    @Override
    public boolean existsById(Long id) {
        return jpaItemTypeRepository.existsById(id);
    }
}
