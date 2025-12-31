package me.aco.marketplace_spring_ca.integration;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.asyncDispatch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import me.aco.marketplace_spring_ca.application.usecases.auth.command.RegisterCommand;
import me.aco.marketplace_spring_ca.application.usecases.item.command.AddItemCommand;
import me.aco.marketplace_spring_ca.infrastructure.persistence.JpaItemRepository;
import me.aco.marketplace_spring_ca.infrastructure.persistence.JpaUserRepository;

@SpringBootTest
@AutoConfigureMockMvc
public class AddItemDeactivateActivateAndDelete {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JpaUserRepository jpaUserRepository;
    @Autowired
    private JpaItemRepository jpaItemRepository;

    ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void testAddItemDeactivateActivateAndDelete() throws Exception {
        
        // Step 1: Create new User
        RegisterCommand registerCommand = new RegisterCommand(
                "newuser",
                "password123",
                "newuser@example.com",
                "New User",
                "555-9999"
        );

        mockMvc.perform(post("/api/Auth/register")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(registerCommand)))
            .andExpect(status().isOk())
            .andExpect(request().asyncStarted())
            .andDo(result -> mockMvc.perform(asyncDispatch(result))
            .andExpect(status().isCreated()));

        // Assert user created
        var createdUserOpt = jpaUserRepository.findSingleByUsername("newuser");
        assertTrue(createdUserOpt.isPresent());

        // Step 2: Add a new item
        AddItemCommand addItemCommand = new AddItemCommand(
            "Test Item 1",
            "Description 1",
            new BigDecimal("99.99"),
            1L,
            createdUserOpt.get().getId()
        );

        mockMvc.perform(post("/api/Item")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(addItemCommand)))
            .andExpect(status().isOk())
            .andExpect(request().asyncStarted())
            .andDo(result -> mockMvc.perform(asyncDispatch(result))
            .andExpect(status().isCreated()));

        // Assert item created
        var createdItemOpt = jpaItemRepository.findBySeller(createdUserOpt.get()).stream()
            .filter(i -> i.getName().equals("Test Item 1"))
            .findFirst();
        assertTrue(createdItemOpt.isPresent());

        // Step 3: Deactivate the item
        mockMvc.perform(put("/api/Item/Deactivate/" + createdItemOpt.get().getId()))
            .andExpect(status().isOk())
            .andExpect(request().asyncStarted())
            .andDo(result -> mockMvc.perform(asyncDispatch(result))
            .andExpect(status().isOk()));

        // Assert item is deactivated
        var deactivatedItemOpt = jpaItemRepository.findById(createdItemOpt.get().getId());
        assertTrue(deactivatedItemOpt.isPresent());
        assertTrue(!deactivatedItemOpt.get().isActive());

        // Step 4: Activate the item
        mockMvc.perform(put("/api/Item/Activate/" + createdItemOpt.get().getId()))
            .andExpect(status().isOk())
            .andExpect(request().asyncStarted())
            .andDo(result -> mockMvc.perform(asyncDispatch(result))
            .andExpect(status().isOk()));

        // Assert item is activated
        var activatedItemOpt = jpaItemRepository.findById(createdItemOpt.get().getId());
        assertTrue(activatedItemOpt.isPresent());
        assertTrue(activatedItemOpt.get().isActive());

        // Step 5: Delete the item
        mockMvc.perform(post("/api/Item/Delete/" + createdItemOpt.get().getId()))
            .andExpect(status().isOk())
            .andExpect(request().asyncStarted())
            .andDo(result -> mockMvc.perform(asyncDispatch(result))
            .andExpect(status().isNoContent()));

    }
    
}
    