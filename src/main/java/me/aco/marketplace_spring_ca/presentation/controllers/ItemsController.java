package me.aco.marketplace_spring_ca.presentation.controllers;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import me.aco.marketplace_spring_ca.application.dto.ItemDto;
import me.aco.marketplace_spring_ca.application.dto.ItemTypeDto;
import me.aco.marketplace_spring_ca.application.usecases.item.command.ActivateItemCommand;
import me.aco.marketplace_spring_ca.application.usecases.item.command.ActivateItemCommandHandler;
import me.aco.marketplace_spring_ca.application.usecases.item.command.AddItemCommand;
import me.aco.marketplace_spring_ca.application.usecases.item.command.AddItemCommandHandler;
import me.aco.marketplace_spring_ca.application.usecases.item.command.DeactivateItemCommand;
import me.aco.marketplace_spring_ca.application.usecases.item.command.DeactivateItemCommandHandler;
import me.aco.marketplace_spring_ca.application.usecases.item.command.DeleteItemCommand;
import me.aco.marketplace_spring_ca.application.usecases.item.command.DeleteItemCommandHandler;
import me.aco.marketplace_spring_ca.application.usecases.item.command.UpdateItemCommand;
import me.aco.marketplace_spring_ca.application.usecases.item.command.UpdateItemCommandHandler;
import me.aco.marketplace_spring_ca.application.usecases.item.query.GetItemByIdQuery;
import me.aco.marketplace_spring_ca.application.usecases.item.query.GetItemByIdQueryHandler;
import me.aco.marketplace_spring_ca.application.usecases.item.query.GetItemsByItemTypeQuery;
import me.aco.marketplace_spring_ca.application.usecases.item.query.GetItemsByItemTypeQueryHandler;
import me.aco.marketplace_spring_ca.application.usecases.item.query.GetItemsBySellerQuery;
import me.aco.marketplace_spring_ca.application.usecases.item.query.GetItemsBySellerQueryHandler;
import me.aco.marketplace_spring_ca.application.usecases.itemType.query.GetItemTypesQuery;
import me.aco.marketplace_spring_ca.application.usecases.itemType.query.GetItemTypesQueryHandler;

@RestController
@RequestMapping("/api/Item")
public class ItemsController extends BaseController {
    
    private final GetItemByIdQueryHandler getItemByIdQueryHandler;
    private final GetItemsByItemTypeQueryHandler getItemsByItemTypeQueryHandler;
    private final GetItemsBySellerQueryHandler getItemsBySellerQueryHandler;
    private final AddItemCommandHandler addItemCommandHandler;
    private final UpdateItemCommandHandler updateItemCommandHandler;
    private final DeactivateItemCommandHandler deactivateItemCommandHandler;
    private final ActivateItemCommandHandler activateItemCommandHandler;
    private final DeleteItemCommandHandler deleteItemCommandHandler;
    private final GetItemTypesQueryHandler getItemTypesQueryHandler;

    public ItemsController(GetItemByIdQueryHandler getItemByIdQueryHandler,
            GetItemsByItemTypeQueryHandler getItemsByItemTypeQueryHandler,
            GetItemsBySellerQueryHandler getItemsBySellerQueryHandler,
            AddItemCommandHandler addItemCommandHandler,
            UpdateItemCommandHandler updateItemCommandHandler,
            DeactivateItemCommandHandler deactivateItemCommandHandler,
            ActivateItemCommandHandler activateItemCommandHandler,
            DeleteItemCommandHandler deleteItemCommandHandler,
            GetItemTypesQueryHandler getItemTypesQueryHandler) {
        this.getItemByIdQueryHandler = getItemByIdQueryHandler;
        this.getItemsByItemTypeQueryHandler = getItemsByItemTypeQueryHandler;
        this.getItemsBySellerQueryHandler = getItemsBySellerQueryHandler;
        this.addItemCommandHandler = addItemCommandHandler;
        this.updateItemCommandHandler = updateItemCommandHandler;
        this.deactivateItemCommandHandler = deactivateItemCommandHandler;
        this.activateItemCommandHandler = activateItemCommandHandler;
        this.deleteItemCommandHandler = deleteItemCommandHandler;
        this.getItemTypesQueryHandler = getItemTypesQueryHandler;
    }

    @GetMapping("/{id}")
    public CompletableFuture<ResponseEntity<ItemDto>> getItemById(@PathVariable Long id) {
        return getItemByIdQueryHandler.handle(new GetItemByIdQuery(id))
                .thenApply(ResponseEntity::ok);
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
    public CompletableFuture<ResponseEntity<ItemDto>> createItem(@RequestBody AddItemCommand command) {
        return addItemCommandHandler.handle(command)
                .thenApply(this::created);
    }

    @PostMapping("/{itemId}")
    public CompletableFuture<ResponseEntity<ItemDto>> updateItem(@PathVariable Long itemId, @RequestBody UpdateItemCommand command) {
        return updateItemCommandHandler.handle(command)
                .thenApply(ResponseEntity::ok);
    }

    @PostMapping("/Deactivate/{id}")
    public CompletableFuture<ResponseEntity<ItemDto>> deactivateItem(@PathVariable DeactivateItemCommand command) {
        return deactivateItemCommandHandler.handle(command)
                .thenApply(ResponseEntity::ok);
    }

    @PostMapping("/Activate/{id}")
    public CompletableFuture<ResponseEntity<ItemDto>> activateItem(@PathVariable ActivateItemCommand command) {
        return activateItemCommandHandler.handle(command)
                .thenApply(ResponseEntity::ok);
    }

    @PostMapping("/Delete/{id}")
    public CompletableFuture<ResponseEntity<Void>> deleteItem(@PathVariable DeleteItemCommand command) {
        return deleteItemCommandHandler.handle(command)
                .thenApply(this::noContent);
    }

    @GetMapping("/Types")
    public CompletableFuture<ResponseEntity<List<ItemTypeDto>>> getItemTypes(GetItemTypesQuery query) {
        return getItemTypesQueryHandler.handle(query)
                .thenApply(ResponseEntity::ok);
    }
}