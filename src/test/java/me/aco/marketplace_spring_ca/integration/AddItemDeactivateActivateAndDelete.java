package me.aco.marketplace_spring_ca.integration;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import me.aco.marketplace_spring_ca.application.dto.TokenDto;
import me.aco.marketplace_spring_ca.application.usecases.auth.command.LoginCommand;
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

        String suffix = UUID.randomUUID().toString();
        String username = "newuser_" + suffix;
        String email = "newuser_" + suffix + "@example.com";
        String itemName = "Test Item 1 " + suffix;
        
        // Step 1: Create new User
        RegisterCommand registerCommand = new RegisterCommand(
            username,
                "password123",
            email,
                "New User",
                "555-9999"
        );

        mockMvc.perform(post("/api/Auth/register")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(registerCommand)))
            .andExpect(status().isCreated());

        // Assert user created
        var createdUserOpt = jpaUserRepository.findSingleByUsername(username);
        assertTrue(createdUserOpt.isPresent());

        // Step 2: Login and get JWT token
        LoginCommand loginCommand = new LoginCommand(username, "password123");
        var loginResponse = mockMvc.perform(post("/api/Auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(loginCommand)))
            .andExpect(status().isOk())
            .andReturn();
        
        TokenDto tokenDto = objectMapper.readValue(loginResponse.getResponse().getContentAsString(), TokenDto.class);
        String jwtToken = "Bearer " + tokenDto.accessToken();

        // Step 3: Add a new item
        AddItemCommand addItemCommand = new AddItemCommand(
            itemName,
            "Description 1",
            new BigDecimal("99.99"),
            1L,
            createdUserOpt.get().getId()
        );

        mockMvc.perform(post("/api/Item")
            .contentType(MediaType.APPLICATION_JSON)
            .header("Authorization", jwtToken)
            .content(objectMapper.writeValueAsString(addItemCommand)))
            .andExpect(status().isCreated());

        // Assert item created
        var createdItemOpt = jpaItemRepository.findBySeller(createdUserOpt.get()).stream()
            .filter(i -> i.getName().equals(itemName))
            .findFirst();
        assertTrue(createdItemOpt.isPresent());

        // Step 4: Deactivate the item
        mockMvc.perform(put("/api/Item/Deactivate/" + createdItemOpt.get().getId())
            .header("Authorization", jwtToken))
            .andExpect(status().isOk());

        // Assert item is deactivated
        var deactivatedItemOpt = jpaItemRepository.findById(createdItemOpt.get().getId());
        assertTrue(deactivatedItemOpt.isPresent());
        assertTrue(!deactivatedItemOpt.get().isActive());

        // Step 5: Activate the item
        mockMvc.perform(put("/api/Item/Activate/" + createdItemOpt.get().getId())
            .header("Authorization", jwtToken))
            .andExpect(status().isOk());

        // Assert item is activated
        var activatedItemOpt = jpaItemRepository.findById(createdItemOpt.get().getId());
        assertTrue(activatedItemOpt.isPresent());
        assertTrue(activatedItemOpt.get().isActive());

        // Step 6: Delete the item
        mockMvc.perform(post("/api/Item/Delete/" + createdItemOpt.get().getId())
            .header("Authorization", jwtToken))
            .andExpect(status().isNoContent());

    }
    
}
    