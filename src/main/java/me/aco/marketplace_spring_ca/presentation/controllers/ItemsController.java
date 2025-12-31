package me.aco.marketplace_spring_ca.presentation.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
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

    @GetMapping("/{id}")
    public ResponseEntity<ItemDto> getItemById(@PathVariable Long id) {
        return ok(getItemByIdQueryHandler.handle(new GetItemByIdQuery(id)));
    }

    @GetMapping("/bySellerId/{sellerId}")
    public ResponseEntity<List<ItemDto>> getItemsBySellerId(@PathVariable Long sellerId) {
        return ok(getItemsBySellerQueryHandler.handle(new GetItemsBySellerQuery(sellerId)));
    }

    @GetMapping("/byTypeId/{typeId}")
    public ResponseEntity<List<ItemDto>> getItemsByTypeId(@PathVariable Long typeId) {
        return ok(getItemsByItemTypeQueryHandler.handle(new GetItemsByItemTypeQuery(typeId)));
    }

    @PostMapping
    public ResponseEntity<ItemDto> createItem(@RequestBody AddItemCommand command) {
        return created(addItemCommandHandler.handle(command));
    }

    @PostMapping("/{itemId}")
    public ResponseEntity<ItemDto> updateItem(@PathVariable Long itemId, @RequestBody UpdateItemCommand command) {
        return ok(updateItemCommandHandler.handle(UpdateItemCommand.withId(itemId, command)));
    }

    @PutMapping("/Deactivate/{id}")
    public ResponseEntity<ItemDto> deactivateItem(@PathVariable Long id) {
        return ok(deactivateItemCommandHandler.handle(new DeactivateItemCommand(id)));
    }

    @PutMapping("/Activate/{id}")
    public ResponseEntity<ItemDto> activateItem(@PathVariable Long id) {
        return ok(activateItemCommandHandler.handle(new ActivateItemCommand(id)));
    }

    @PostMapping("/Delete/{id}")
    public ResponseEntity<Void> deleteItem(@PathVariable Long id) {
        deleteItemCommandHandler.handle(new DeleteItemCommand(id));
        return noContent(null);
    }

    @GetMapping("/Types")
    public ResponseEntity<List<ItemTypeDto>> getItemTypes(GetItemTypesQuery query) {
        return ok(getItemTypesQueryHandler.handle(query));
    }
}