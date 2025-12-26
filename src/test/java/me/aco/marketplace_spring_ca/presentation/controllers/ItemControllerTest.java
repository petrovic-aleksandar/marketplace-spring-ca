package me.aco.marketplace_spring_ca.presentation.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.asyncDispatch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import me.aco.marketplace_spring_ca.application.dto.ItemDto;
import me.aco.marketplace_spring_ca.application.exceptions.ResourceNotFoundException;
import me.aco.marketplace_spring_ca.application.usecases.item.command.ActivateItemCommandHandler;
import me.aco.marketplace_spring_ca.application.usecases.item.command.AddItemCommand;
import me.aco.marketplace_spring_ca.application.usecases.item.command.AddItemCommandHandler;
import me.aco.marketplace_spring_ca.application.usecases.item.command.DeactivateItemCommandHandler;
import me.aco.marketplace_spring_ca.application.usecases.item.command.DeleteItemCommandHandler;
import me.aco.marketplace_spring_ca.application.usecases.item.command.UpdateItemCommand;
import me.aco.marketplace_spring_ca.application.usecases.item.command.UpdateItemCommandHandler;
import me.aco.marketplace_spring_ca.application.usecases.item.query.GetItemByIdQueryHandler;
import me.aco.marketplace_spring_ca.application.usecases.item.query.GetItemsByItemTypeQueryHandler;
import me.aco.marketplace_spring_ca.application.usecases.item.query.GetItemsBySellerQueryHandler;
import me.aco.marketplace_spring_ca.application.usecases.itemType.query.GetItemTypesQueryHandler;

@WebMvcTest(ItemsController.class)
public class ItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private GetItemByIdQueryHandler getItemByIdQueryHandler;
    @MockitoBean
    private GetItemsBySellerQueryHandler getItemsBySellerQueryHandler;
    @MockitoBean
    private GetItemsByItemTypeQueryHandler getItemsByItemTypeQueryHandler;
    @MockitoBean
    private AddItemCommandHandler addItemCommandHandler;
    @MockitoBean
    private UpdateItemCommandHandler updateItemCommandHandler;
    @MockitoBean
    private DeactivateItemCommandHandler deactivateItemCommandHandler;
    @MockitoBean
    private ActivateItemCommandHandler activateItemCommandHandler;
    @MockitoBean
    private DeleteItemCommandHandler deleteItemCommandHandler;
    @MockitoBean
    private GetItemTypesQueryHandler getItemTypesQueryHandler;

    private ItemDto mockItem1;
    private ItemDto mockItem1Updated;
    private ItemDto mockItem1Inactive;
    private ItemDto mockItem2;
    private List<ItemDto> mockItems;
    private AddItemCommand validAddItemCommand;
    private UpdateItemCommand validUpdateItemCommand;

    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {

        mockItem1 = new ItemDto(
            1L,
            "Test Item 1",
            "Description 1",
            99.99,
            null,
            true,
            false,
            "2025-12-26T12:00:00Z",
            null,
            null
        );

        mockItem1Updated = new ItemDto(
            1L,
            "Test Item 1",
            "Description 1 CHANGED",
            99.99,
            null,
            true,
            false,
            "2025-12-26T12:00:00Z",
            null,
            null
        );

        mockItem1Inactive = new ItemDto(
            1L,
            "Test Item 1",
            "Description 1",
            99.99,
            null,
            false,
            false,
            "2025-12-26T12:00:00Z",
            null,
            null
        );

        mockItem2 = new ItemDto(
            2L,
            "Test Item 2",
            "Description 2",
            199.99,
            null,
            true,
            false,
            "2025-12-26T12:00:00Z",
            null,
            null
        );

        mockItems = List.of(mockItem1, mockItem2);

        validAddItemCommand = new AddItemCommand(
            "Test Item 1",
            "Description 1",
            99.99,
            1L,
            1L
        ); 

        validUpdateItemCommand = new UpdateItemCommand(
            1L,
            "Test Item 1",
            "Description 1 CHANGED",
            99.99,
            1L,
            1L
        );
    }

    @Test
    void shouldReturnOkWhenGetItemById() throws Exception {

        // Arrange
        when(getItemByIdQueryHandler.handle(any()))
            .thenReturn(CompletableFuture.completedFuture(mockItem1));

        // Act & Assert
        mockMvc.perform(get("/api/Item/1"))
                .andExpect(request().asyncStarted())
                .andExpect(status().isOk())
                .andDo(result -> mockMvc.perform(asyncDispatch(result)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Test Item 1"));
    }

    @Test
    void shouldThrowExceptionWhenGetItemById() throws Exception {

        // Arrange
        when(getItemByIdQueryHandler.handle(any()))
            .thenReturn(CompletableFuture.failedFuture(new ResourceNotFoundException("Item not found")));

        // Act & Assert
        mockMvc.perform(get("/api/Item/1"))
                .andExpect(request().asyncStarted())
                .andExpect(status().isOk())
                .andDo(result -> mockMvc.perform(asyncDispatch(result)))
                .andExpect(status().isNotFound());
    }
    
    @Test
    void shouldReturnOkWhenGetItemsBySeller() throws Exception {
        
        // Arrange
        when(getItemsBySellerQueryHandler.handle(any()))
            .thenReturn(CompletableFuture.completedFuture(mockItems));

        // Act & Assert
        mockMvc.perform(get("/api/Item/bySellerId/1"))
                .andExpect(status().isOk())
                .andExpect(request().asyncStarted())
                .andDo(result -> mockMvc.perform(asyncDispatch(result)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("Test Item 1"))
                .andExpect(jsonPath("$[1].name").value("Test Item 2"));
    }

    @Test
    void shouldThrowExceptionWhenGetItemsBySeller() throws Exception {
        
        // Arrange
        when(getItemsBySellerQueryHandler.handle(any()))
            .thenReturn(CompletableFuture.failedFuture(new ResourceNotFoundException("Seller not found")));

        // Act & Assert
        mockMvc.perform(get("/api/Item/bySellerId/1"))
                .andExpect(status().isOk())
                .andExpect(request().asyncStarted())
                .andDo(result -> mockMvc.perform(asyncDispatch(result)))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnOkWhenGetItemsByType() throws Exception {
        
        // Arrange
        when(getItemsByItemTypeQueryHandler.handle(any()))
            .thenReturn(CompletableFuture.completedFuture(mockItems));

        // Act & Assert
        mockMvc.perform(get("/api/Item/byTypeId/1"))
                .andExpect(status().isOk())
                .andExpect(request().asyncStarted())
                .andDo(result -> mockMvc.perform(asyncDispatch(result)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("Test Item 1"))
                .andExpect(jsonPath("$[1].name").value("Test Item 2"));
    }

    @Test
    void shouldThrowExceptionWhenGetItemsByType() throws Exception {
        
        // Arrange
        when(getItemsByItemTypeQueryHandler.handle(any()))
            .thenReturn(CompletableFuture.failedFuture(new ResourceNotFoundException("Item type not found")));

        // Act & Assert
        mockMvc.perform(get("/api/Item/byTypeId/1"))
                .andExpect(status().isOk())
                .andExpect(request().asyncStarted())
                .andDo(result -> mockMvc.perform(asyncDispatch(result)))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnCreatedWhenAddItem() throws Exception {

        // Arrange
        when(addItemCommandHandler.handle(any()))
            .thenReturn(CompletableFuture.completedFuture(mockItem1));

        // Act & Assert
        mockMvc.perform(post("/api/Item")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validAddItemCommand)))
                .andExpect(status().isOk())
                .andExpect(request().asyncStarted())
                .andDo(result -> mockMvc.perform(asyncDispatch(result)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Test Item 1"));
    }

    @Test
    void shouldReturnOkWhenUpdateItem() throws Exception {

        // Arrange
        when(updateItemCommandHandler.handle(any()))
            .thenReturn(CompletableFuture.completedFuture(mockItem1Updated));

        // Act & Assert
        mockMvc.perform(post("/api/Item/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validUpdateItemCommand)))
                .andExpect(status().isOk())
                .andExpect(request().asyncStarted())
                .andDo(result -> mockMvc.perform(asyncDispatch(result)))
                .andExpect(status().isOk());
    }

    @Test
    void shouldThrowExceptionWhenUpdateItem() throws Exception {

        // Arrange
        when(updateItemCommandHandler.handle(any()))
            .thenReturn(CompletableFuture.failedFuture(new ResourceNotFoundException("Item not found")));

        // Act & Assert
        mockMvc.perform(post("/api/Item/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validUpdateItemCommand)))
                .andExpect(status().isOk())
                .andExpect(request().asyncStarted())
                .andDo(result -> mockMvc.perform(asyncDispatch(result)))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnOkWhenActivateItem() throws Exception {

        // Arrange
        when(activateItemCommandHandler.handle(any()))
            .thenReturn(CompletableFuture.completedFuture(mockItem1));

        // Act & Assert
        mockMvc.perform(post("/api/Item/Activate/1"))
                .andExpect(status().isOk())
                .andExpect(request().asyncStarted())
                .andDo(result -> mockMvc.perform(asyncDispatch(result)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Test Item 1"))
                .andExpect(jsonPath("$.active").value(true));
    }

    @Test
    void shouldThrowExceptionWhenActivateItem() throws Exception {

        // Arrange
        when(activateItemCommandHandler.handle(any()))
            .thenReturn(CompletableFuture.failedFuture(new ResourceNotFoundException("Item not found")));
        // Act & Assert
        mockMvc.perform(post("/api/Item/Activate/1"))
                .andExpect(status().isOk())
                .andExpect(request().asyncStarted())
                .andDo(result -> mockMvc.perform(asyncDispatch(result)))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnOkWhenDeactivateItem() throws Exception {
        
        // Arrange
        when(deactivateItemCommandHandler.handle(any()))
            .thenReturn(CompletableFuture.completedFuture(mockItem1Inactive));

        // Act & Assert
        mockMvc.perform(post("/api/Item/Deactivate/1"))
                .andExpect(status().isOk())
                .andExpect(request().asyncStarted())
                .andDo(result -> mockMvc.perform(asyncDispatch(result)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Test Item 1"))
                .andExpect(jsonPath("$.active").value(false));
    }

    @Test
    void shouldThrowExceptionWhenDeactivateItem() throws Exception {
        
        // Arrange
        when(deactivateItemCommandHandler.handle(any()))
            .thenReturn(CompletableFuture.failedFuture(new ResourceNotFoundException("Item not found")));
        // Act & Assert
        mockMvc.perform(post("/api/Item/Deactivate/1"))
                .andExpect(status().isOk())
                .andExpect(request().asyncStarted())
                .andDo(result -> mockMvc.perform(asyncDispatch(result)))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnNoContentWhenDeleteItem() throws Exception {
        
        // Arrange
        when(deleteItemCommandHandler.handle(any()))
            .thenReturn(CompletableFuture.completedFuture(null));

        // Act & Assert
        mockMvc.perform(post("/api/Item/Delete/1"))
                .andExpect(status().isOk())
                .andExpect(request().asyncStarted())
                .andDo(result -> mockMvc.perform(asyncDispatch(result)))
                .andExpect(status().isNoContent());
    }

    @Test
    void shouldThrowExceptionWhenDeleteItem() throws Exception {
        
        // Arrange
        when(deleteItemCommandHandler.handle(any()))
            .thenReturn(CompletableFuture.failedFuture(new ResourceNotFoundException("Item not found")));

        // Act & Assert
        mockMvc.perform(post("/api/Item/Delete/1"))
                .andExpect(status().isOk())
                .andExpect(request().asyncStarted())
                .andDo(result -> mockMvc.perform(asyncDispatch(result)))
                .andExpect(status().isNotFound());
    }
    
    
}
