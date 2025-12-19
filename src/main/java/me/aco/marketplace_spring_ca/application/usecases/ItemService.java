package me.aco.marketplace_spring_ca.application.usecases;

import java.math.BigDecimal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import me.aco.marketplace_spring_ca.application.dto.ItemReq;
import me.aco.marketplace_spring_ca.application.dto.ItemResp;
import me.aco.marketplace_spring_ca.domain.entities.Item;
import me.aco.marketplace_spring_ca.domain.entities.ItemType;
import me.aco.marketplace_spring_ca.domain.entities.User;
import me.aco.marketplace_spring_ca.infrastructure.persistence.JpaItemRepository;

@Service
public class ItemService {

    @Autowired
    private JpaItemRepository itemRepository;

    public ItemResp add(ItemReq req, ItemType type, User seller) {
        Item item = new Item();
        item.setName(req.getName());
        item.setDescription(req.getDescription());
        item.setPrice(BigDecimal.valueOf(req.getPrice()));
        item.setSeller(seller);
        item.setType(type);
        Item savedItem = itemRepository.save(item);
        return new ItemResp(savedItem);
    }

    public ItemResp update(ItemReq req, ItemType type, User seller, Item existingItem) {
        existingItem.setName(req.getName());
        existingItem.setDescription(req.getDescription());
        existingItem.setPrice(BigDecimal.valueOf(req.getPrice()));
        existingItem.setSeller(seller);
        existingItem.setType(type);
        Item updatedItem = itemRepository.save(existingItem);
        return new ItemResp(updatedItem);
    }
}
