package me.aco.marketplace_spring_ca.presentation.controllers;

import java.util.List;
import java.util.concurrent.CompletableFuture;
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
import me.aco.marketplace_spring_ca.application.dto.ItemDto;
import me.aco.marketplace_spring_ca.application.dto.ItemTypeDto;
import me.aco.marketplace_spring_ca.application.exceptions.ResourceNotFoundException;
import me.aco.marketplace_spring_ca.infrastructure.persistence.JpaItemRepository;
import me.aco.marketplace_spring_ca.infrastructure.persistence.JpaItemTypeRepository;
import me.aco.marketplace_spring_ca.infrastructure.persistence.JpaUserRepository;
import me.aco.marketplace_spring_ca.application.usecases.ItemService;
import me.aco.marketplace_spring_ca.application.usecases.item.query.GetItemsByItemType;
import me.aco.marketplace_spring_ca.application.usecases.item.query.GetItemsByItemTypeQuery;
import me.aco.marketplace_spring_ca.application.usecases.item.query.GetItemsByItemTypeQueryHandler;
import me.aco.marketplace_spring_ca.application.usecases.item.query.GetItemsBySellerQuery;
import me.aco.marketplace_spring_ca.application.usecases.item.query.GetItemsBySellerQueryHandler;
import me.aco.marketplace_spring_ca.application.usecases.itemType.query.GetItemTypesQuery;
import me.aco.marketplace_spring_ca.application.usecases.itemType.query.GetItemTypesQueryHandler;

@RestController
@RequestMapping("/api/Item")
public class ItemsController {
    
    private final JpaItemRepository itemRepository;
    private final JpaUserRepository userRepository;
    private final ItemService itemService;
    private final JpaItemTypeRepository itemTypeRepository;
    private final GetItemTypesQueryHandler getItemTypesQueryHandler;
    private final GetItemsByItemTypeQueryHandler getItemsByItemTypeQueryHandler;
    private final GetItemsBySellerQueryHandler getItemsBySellerQueryHandler;

    public ItemsController(JpaItemRepository itemRepository, JpaUserRepository userRepository, JpaItemTypeRepository itemTypeRepository,
                           GetItemTypesQueryHandler getItemTypesQueryHandler, GetItemsByItemTypeQueryHandler getItemsByItemTypeQueryHandler, GetItemsBySellerQueryHandler getItemsBySellerQueryHandler, ItemService itemService) {
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
        this.itemService = itemService;
        this.itemTypeRepository = itemTypeRepository;
        this.getItemTypesQueryHandler = getItemTypesQueryHandler;
        this.getItemsByItemTypeQueryHandler = getItemsByItemTypeQueryHandler;
        this.getItemsBySellerQueryHandler = getItemsBySellerQueryHandler;
    }

    @GetMapping("/{id}")
    public ResponseEntity<ItemDto> getItemById(@PathVariable Long id) {
        var item = itemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Item not found"));
        return ResponseEntity.ok(new ItemDto(item));
    }

    @GetMapping("/bySellerId/{sellerId}")
    public CompletableFuture<ResponseEntity<List<ItemDto>>> getItemsBySellerId(@PathVariable Long sellerId) {
        return getItemsBySellerQueryHandler.handle(new GetItemsBySellerQuery(sellerId))
                .thenApply(ResponseEntity::ok);
    }

    @GetMapping("/byTypeId/{typeId}")
    public CompletableFuture<ResponseEntity<List<ItemDto>>> getItemsByTypeId(@PathVariable Long typeId) {
        return getItemsByItemTypeQueryHandler.handle(new GetItemsByItemTypeQuery(typeId))
                .thenApply(ResponseEntity::ok);
    }

    @PostMapping
    public ResponseEntity<ItemDto> addItem(@RequestBody ItemReq req) {
        var seller = userRepository.findById(req.getSellerId())
                .orElseThrow(() -> new ResourceNotFoundException("Seller not found"));
        var itemType = itemTypeRepository.findById(req.getTypeId())
                .orElseThrow(() -> new ResourceNotFoundException("Item type not found"));
        var addedItem = itemService.add(req, itemType, seller);
        return ResponseEntity.status(HttpStatus.CREATED).body(addedItem);
    }

    @PostMapping("/{itemId}")
    public ResponseEntity<ItemDto> updateItem(@PathVariable Long itemId, @RequestBody ItemReq req) {
        var existingItem = itemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Item not found"));
        var seller = userRepository.findById(req.getSellerId())
                .orElseThrow(() -> new ResourceNotFoundException("Seller not found"));
        var itemType = itemTypeRepository.findById(req.getTypeId())
                .orElseThrow(() -> new ResourceNotFoundException("Item type not found"));
        var updatedItem = itemService.update(req, itemType, seller, existingItem);
        return ResponseEntity.ok(updatedItem);
    }

    @PostMapping("/Deactivate/{id}")
    public ResponseEntity<ItemDto> deactivateItem(@PathVariable Long id) {
        var item = itemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Item not found"));
        item.deactivate();
        var updatedItem = itemRepository.save(item);
        return ResponseEntity.ok(new ItemDto(updatedItem));
    }

    @PostMapping("/Activate/{id}")
    public ResponseEntity<ItemDto> activateItem(@PathVariable Long id) {
        var item = itemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Item not found"));
        item.activate();
        var updatedItem = itemRepository.save(item);
        return ResponseEntity.ok(new ItemDto(updatedItem));
    }

    @PostMapping("/Delete/{id}")
    public ResponseEntity<ItemDto> deleteItem(@PathVariable Long id) {
        var item = itemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Item not found"));
        item.softDelete();
        var updatedItem = itemRepository.save(item);
        return ResponseEntity.ok(new ItemDto(updatedItem));
    }

    @GetMapping("/Types")
    public CompletableFuture<ResponseEntity<List<ItemTypeDto>>> getItemTypes(GetItemTypesQuery query) {
        return getItemTypesQueryHandler.handle(query)
                .thenApply(ResponseEntity::ok);
    }
}