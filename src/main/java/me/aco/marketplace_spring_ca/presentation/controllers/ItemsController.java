package me.aco.marketplace_spring_ca.presentation.controllers;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import me.aco.marketplace_spring_ca.application.dto.ItemReq;
import me.aco.marketplace_spring_ca.application.dto.ItemResp;
import me.aco.marketplace_spring_ca.application.exceptions.ResourceNotFoundException;
import me.aco.marketplace_spring_ca.domain.entities.ItemType;
import me.aco.marketplace_spring_ca.infrastructure.persistence.JpaItemRepository;
import me.aco.marketplace_spring_ca.infrastructure.persistence.JpaItemTypeRepository;
import me.aco.marketplace_spring_ca.infrastructure.persistence.JpaUserRepository;
import me.aco.marketplace_spring_ca.application.usecases.ItemService;

@RestController
@RequestMapping("/api/items")
public class ItemsController {
    
    private final JpaItemRepository itemRepository;
    private final JpaUserRepository userRepository;
    private final JpaItemTypeRepository itemTypeRepository;
    private final ItemService itemService;

    public ItemsController(JpaItemRepository itemRepository, JpaUserRepository userRepository,
                           JpaItemTypeRepository itemTypeRepository, ItemService itemService) {
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
        this.itemTypeRepository = itemTypeRepository;
        this.itemService = itemService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<ItemResp> getItemById(@PathVariable Long id) {
        var item = itemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Item not found"));
        return ResponseEntity.ok(new ItemResp(item));
    }

    @GetMapping("/seller/{sellerId}")
    public ResponseEntity<List<ItemResp>> getItemsBySellerId(@PathVariable Long sellerId) {
        var seller = userRepository.findById(sellerId)
                .orElseThrow(() -> new ResourceNotFoundException("Seller not found"));
        var items = itemRepository.findBySeller(seller);
        var resp = items.stream().map(ItemResp::new).collect(Collectors.toList());
        return ResponseEntity.ok(resp);
    }

    @GetMapping("/type/{typeId}")
    public ResponseEntity<List<ItemResp>> getItemsByTypeId(@PathVariable Long typeId) {
        var type = itemTypeRepository.findById(typeId)
                .orElseThrow(() -> new ResourceNotFoundException("Item type not found"));
        var items = itemRepository.findByType(type);
        var resp = items.stream().map(ItemResp::new).collect(Collectors.toList());
        return ResponseEntity.ok(resp);
    }

    @PostMapping
    public ResponseEntity<ItemResp> addItem(@RequestBody ItemReq req) {
        var seller = userRepository.findById(req.getSellerId())
                .orElseThrow(() -> new ResourceNotFoundException("Seller not found"));
        var itemType = itemTypeRepository.findById(req.getTypeId())
                .orElseThrow(() -> new ResourceNotFoundException("Item type not found"));
        var addedItem = itemService.add(req, itemType, seller);
        return ResponseEntity.status(HttpStatus.CREATED).body(addedItem);
    }

    @PostMapping("/{itemId}")
    public ResponseEntity<ItemResp> updateItem(@PathVariable Long itemId, @RequestBody ItemReq req) {
        var existingItem = itemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Item not found"));
        var seller = userRepository.findById(req.getSellerId())
                .orElseThrow(() -> new ResourceNotFoundException("Seller not found"));
        var itemType = itemTypeRepository.findById(req.getTypeId())
                .orElseThrow(() -> new ResourceNotFoundException("Item type not found"));
        var updatedItem = itemService.update(req, itemType, seller, existingItem);
        return ResponseEntity.ok(updatedItem);
    }

    @PostMapping("/deactivate/{id}")
    public ResponseEntity<ItemResp> deactivateItem(@PathVariable Long id) {
        var item = itemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Item not found"));
        item.deactivate();
        var updatedItem = itemRepository.save(item);
        return ResponseEntity.ok(new ItemResp(updatedItem));
    }

    @PostMapping("/activate/{id}")
    public ResponseEntity<ItemResp> activateItem(@PathVariable Long id) {
        var item = itemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Item not found"));
        item.activate();
        var updatedItem = itemRepository.save(item);
        return ResponseEntity.ok(new ItemResp(updatedItem));
    }

    @PostMapping("/delete/{id}")
    public ResponseEntity<ItemResp> deleteItem(@PathVariable Long id) {
        var item = itemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Item not found"));
        item.softDelete();
        var updatedItem = itemRepository.save(item);
        return ResponseEntity.ok(new ItemResp(updatedItem));
    }

    @GetMapping("/types")
    public ResponseEntity<List<ItemType>> getItemTypes() {
        var types = itemTypeRepository.findAll();
        return ResponseEntity.ok(types);
    }
}