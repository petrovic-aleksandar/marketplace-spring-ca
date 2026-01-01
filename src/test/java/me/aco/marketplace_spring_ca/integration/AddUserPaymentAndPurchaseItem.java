package me.aco.marketplace_spring_ca.integration;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;

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
import me.aco.marketplace_spring_ca.application.usecases.transfer.command.AddPaymentCommand;
import me.aco.marketplace_spring_ca.application.usecases.transfer.command.PurchaseItemCommand;
import me.aco.marketplace_spring_ca.infrastructure.persistence.JpaItemRepository;
import me.aco.marketplace_spring_ca.infrastructure.persistence.JpaUserRepository;

@SpringBootTest
@AutoConfigureMockMvc
public class AddUserPaymentAndPurchaseItem {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JpaUserRepository jpaUserRepository;    
    @Autowired
    private JpaItemRepository jpaItemRepository;

    ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void testAddUserPaymentAndPurchaseItem() throws Exception {

        // Step 1: Register new User
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
            .andExpect(status().isCreated());

        // Assert user created, has zero balance
        var createdUserOpt = jpaUserRepository.findSingleByUsername("newuser");
        assertTrue(createdUserOpt.isPresent());
        assertTrue(createdUserOpt.get().getBalance().compareTo(BigDecimal.ZERO) == 0);

        // Step 2: Login and get JWT token for seller
        LoginCommand loginCommand = new LoginCommand("newuser", "password123");
        var loginResponse = mockMvc.perform(post("/api/Auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(loginCommand)))
            .andExpect(status().isOk())
            .andReturn();
        
        TokenDto tokenDto = objectMapper.readValue(loginResponse.getResponse().getContentAsString(), TokenDto.class);
        String sellerToken = "Bearer " + tokenDto.accessToken();

        // Step 3: Add a new item
        AddItemCommand addItemCommand = new AddItemCommand(
            "Test Item 1",
            "Description 1",
            new BigDecimal("100.00"),
            1L,
            createdUserOpt.get().getId()
        );

        mockMvc.perform(post("/api/Item")
            .contentType(MediaType.APPLICATION_JSON)
            .header("Authorization", sellerToken)
            .content(objectMapper.writeValueAsString(addItemCommand)))
            .andExpect(status().isCreated());

        // Assert item created
        var createdItemOpt = jpaItemRepository.findBySeller(createdUserOpt.get()).stream()
            .filter(i -> i.getName().equals("Test Item 1"))
            .findFirst();
        assertTrue(createdItemOpt.isPresent());

        // Step 4: Register Buyer User
        RegisterCommand registerCommand2 = new RegisterCommand(
                "buyeruser",
                "password123",
                "buyeruser@example.com",
                "Buyer User",
                "555-8888"
        );

        mockMvc.perform(post("/api/Auth/register")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(registerCommand2)))
            .andExpect(status().isCreated());

        // Assert user created, has zero balance
        var createdUserOpt2 = jpaUserRepository.findSingleByUsername("buyeruser");
        assertTrue(createdUserOpt2.isPresent());
        assertTrue(createdUserOpt2.get().getBalance().compareTo(BigDecimal.ZERO) == 0);

        // Step 5: Login and get JWT token for buyer
        LoginCommand buyerLoginCommand = new LoginCommand("buyeruser", "password123");
        var buyerLoginResponse = mockMvc.perform(post("/api/Auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(buyerLoginCommand)))
            .andExpect(status().isOk())
            .andReturn();
        
        TokenDto buyerTokenDto = objectMapper.readValue(buyerLoginResponse.getResponse().getContentAsString(), TokenDto.class);
        String buyerToken = "Bearer " + buyerTokenDto.accessToken();

        // Step 6: Add Payment
        AddPaymentCommand addPaymentCommand = new AddPaymentCommand(
            createdUserOpt2.get().getId(),
            BigDecimal.valueOf(100.00)
        );

        mockMvc.perform(post("/api/Transfer/payment")
            .contentType(MediaType.APPLICATION_JSON)
            .header("Authorization", buyerToken)
            .content(objectMapper.writeValueAsString(addPaymentCommand)))
            .andExpect(status().isCreated());

        // Assert payment added
        var updatedBuyerOpt = jpaUserRepository.findById(createdUserOpt2.get().getId());
        assertTrue(updatedBuyerOpt.isPresent());
        assertTrue(updatedBuyerOpt.get().getBalance().compareTo(BigDecimal.valueOf(100.00)) == 0);

        // Step 7: Purchase Item
        PurchaseItemCommand purchaseItemCommand = new PurchaseItemCommand(
            createdUserOpt2.get().getId(),
            createdItemOpt.get().getId()
        );

        mockMvc.perform(post("/api/Transfer/purchase")
            .contentType(MediaType.APPLICATION_JSON)
            .header("Authorization", buyerToken)
            .content(objectMapper.writeValueAsString(purchaseItemCommand)))
            .andExpect(status().isCreated());

        // Assert buyer balance deducted
        var finalBuyerOpt = jpaUserRepository.findById(createdUserOpt2.get().getId());
        assertTrue(finalBuyerOpt.isPresent());
        assertTrue(finalBuyerOpt.get().getBalance().compareTo(BigDecimal.ZERO) == 0);

        // Assert item ownership transferred, item deactivated
        var purchasedItemOpt = jpaItemRepository.findById(createdItemOpt.get().getId());
        assertTrue(purchasedItemOpt.isPresent());
        assertTrue(purchasedItemOpt.get().getSeller().getId().equals(createdUserOpt2.get().getId()));
        assertFalse(purchasedItemOpt.get().isActive());

        // Assert seller balance increased
        var finalSellerOpt = jpaUserRepository.findById(createdUserOpt.get().getId());
        assertTrue(finalSellerOpt.isPresent());
        assertTrue(finalSellerOpt.get().getBalance().compareTo(BigDecimal.valueOf(100.00)) == 0);
    }
    
}
