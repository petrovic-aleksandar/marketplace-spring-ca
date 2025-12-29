package me.aco.marketplace_spring_ca.presentation.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.asyncDispatch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.time.LocalDateTime;
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

import me.aco.marketplace_spring_ca.application.dto.TransferDto;
import me.aco.marketplace_spring_ca.application.exceptions.ResourceNotFoundException;
import me.aco.marketplace_spring_ca.application.usecases.transfer.command.AddPaymentCommand;
import me.aco.marketplace_spring_ca.application.usecases.transfer.command.AddPaymentCommandHandler;
import me.aco.marketplace_spring_ca.application.usecases.transfer.command.AddWithdrawalCommand;
import me.aco.marketplace_spring_ca.application.usecases.transfer.command.AddWithdrawalCommandHandler;
import me.aco.marketplace_spring_ca.application.usecases.transfer.command.PurchaseItemCommand;
import me.aco.marketplace_spring_ca.application.usecases.transfer.command.PurchaseItemCommandHandler;
import me.aco.marketplace_spring_ca.application.usecases.transfer.query.GetTransfersByUserQuery;
import me.aco.marketplace_spring_ca.application.usecases.transfer.query.GetTransfersByUserQueryHandler;

@WebMvcTest(TransfersController.class)
class TransferControllerTest {

	@Autowired
	private MockMvc mockMvc;

	private ObjectMapper objectMapper = new ObjectMapper();

	@MockitoBean
	private GetTransfersByUserQueryHandler getTransfersByUserQueryHandler;
	@MockitoBean
	private AddPaymentCommandHandler addPaymentCommandHandler;
	@MockitoBean
	private AddWithdrawalCommandHandler addWithdrawalCommandHandler;
	@MockitoBean
	private PurchaseItemCommandHandler purchaseItemCommandHandler;

	private TransferDto mockTransferDto;

	@BeforeEach
	void setUp() {
		mockTransferDto = new TransferDto(
            1L, 
            BigDecimal.valueOf(100.00), 
            LocalDateTime.now().toString(),
            "PAYMENT",
            null,
            null,
            null
        );
	}

    @Test
    void testGetTransfersByUser() throws Exception {

        // Arrange
        when(getTransfersByUserQueryHandler.handle(any(GetTransfersByUserQuery.class)))
                .thenReturn(CompletableFuture.completedFuture(List.of(mockTransferDto)));

        // Act
        var mvcResult = mockMvc.perform(get("/api/Transfer/byUserId/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(request().asyncStarted())
                .andReturn();

        // Assert
        mockMvc.perform(asyncDispatch(mvcResult))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(mockTransferDto.id()))
                .andExpect(jsonPath("$[0].amount").value(mockTransferDto.amount()));
    }

    @Test
    void testGetTransfersByUser_NotFound() throws Exception {

        // Arrange
        when(getTransfersByUserQueryHandler.handle(any(GetTransfersByUserQuery.class)))
                .thenReturn(CompletableFuture.failedFuture(new ResourceNotFoundException("User not found")));

        // Act
        var mvcResult = mockMvc.perform(get("/api/Transfer/byUserId/999")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(request().asyncStarted())
                .andReturn();

        // Assert
        mockMvc.perform(asyncDispatch(mvcResult))
                .andExpect(status().isNotFound());
    }

	@Test
	void testAddPayment() throws Exception {

        // Arrange
		AddPaymentCommand command = new AddPaymentCommand(1L, BigDecimal.valueOf(100.00));
		when(addPaymentCommandHandler.handle(any(AddPaymentCommand.class)))
				.thenReturn(CompletableFuture.completedFuture(mockTransferDto));

        // Act
		var mvcResult = mockMvc.perform(post("/api/Transfer/payment")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(command)))
				.andExpect(request().asyncStarted())
				.andReturn();

        // Assert
		mockMvc.perform(asyncDispatch(mvcResult))
				.andExpect(status().isCreated());
	}

    @Test
    void testAddPayment_InvalidAmount() throws Exception {

        // Arrange
        AddPaymentCommand command = new AddPaymentCommand(1L, BigDecimal.valueOf(-50.00));
        when(addPaymentCommandHandler.handle(any(AddPaymentCommand.class)))
                .thenReturn(CompletableFuture.failedFuture(new IllegalArgumentException("Amount must be positive")));

        // Act
        var mvcResult = mockMvc.perform(post("/api/Transfer/payment")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(command)))
                .andExpect(request().asyncStarted())
                .andReturn();

        // Assert
        mockMvc.perform(asyncDispatch(mvcResult))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testAddPayment_UserNotFound() throws Exception {

        // Arrange
        AddPaymentCommand command = new AddPaymentCommand(999L, BigDecimal.valueOf(100.00));
        when(addPaymentCommandHandler.handle(any(AddPaymentCommand.class)))
                .thenReturn(CompletableFuture.failedFuture(new ResourceNotFoundException("User not found")));

        // Act
        var mvcResult = mockMvc.perform(post("/api/Transfer/payment")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(command)))
                .andExpect(request().asyncStarted())
                .andReturn();

        // Assert
        mockMvc.perform(asyncDispatch(mvcResult))
                .andExpect(status().isNotFound());
    }

    @Test
    void testAddWithdrawal() throws Exception {

        // Arrange
		AddWithdrawalCommand command = new AddWithdrawalCommand(1L, BigDecimal.valueOf(100.00));
		when(addWithdrawalCommandHandler.handle(any(AddWithdrawalCommand.class)))
				.thenReturn(CompletableFuture.completedFuture(mockTransferDto));

        // Act
		var mvcResult = mockMvc.perform(post("/api/Transfer/withdrawal")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(command)))
				.andExpect(request().asyncStarted())
				.andReturn();

        // Assert
		mockMvc.perform(asyncDispatch(mvcResult))
				.andExpect(status().isCreated());
    }

    @Test
    void testAddWithdrawal_InsufficientBalance() throws Exception {

        // Arrange
        AddWithdrawalCommand command = new AddWithdrawalCommand(1L, BigDecimal.valueOf(1000.00));
        when(addWithdrawalCommandHandler.handle(any(AddWithdrawalCommand.class)))
                .thenReturn(CompletableFuture.failedFuture(new IllegalArgumentException("Insufficient balance")));

        // Act
        var mvcResult = mockMvc.perform(post("/api/Transfer/withdrawal")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(command)))
                .andExpect(request().asyncStarted())
                .andReturn();

        // Assert
        mockMvc.perform(asyncDispatch(mvcResult))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testAddWithdrawal_UserNotFound() throws Exception {

        // Arrange
        AddWithdrawalCommand command = new AddWithdrawalCommand(999L, BigDecimal.valueOf(100.00));
        when(addWithdrawalCommandHandler.handle(any(AddWithdrawalCommand.class)))
                .thenReturn(CompletableFuture.failedFuture(new ResourceNotFoundException("User not found")));

        // Act
        var mvcResult = mockMvc.perform(post("/api/Transfer/withdrawal")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(command)))
                .andExpect(request().asyncStarted())
                .andReturn();

        // Assert
        mockMvc.perform(asyncDispatch(mvcResult))
                .andExpect(status().isNotFound());
    }

    @Test
    void testAddWithdrawal_InvalidAmount() throws Exception {

        // Arrange
        AddWithdrawalCommand command = new AddWithdrawalCommand(1L, BigDecimal.valueOf(-50.00));
        when(addWithdrawalCommandHandler.handle(any(AddWithdrawalCommand.class)))
                .thenReturn(CompletableFuture.failedFuture(new IllegalArgumentException("Amount must be positive")));

        // Act
        var mvcResult = mockMvc.perform(post("/api/Transfer/withdrawal")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(command)))
                .andExpect(request().asyncStarted())
                .andReturn();

        // Assert
        mockMvc.perform(asyncDispatch(mvcResult))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testPurchaseItem() throws Exception {
        // Arrange
        PurchaseItemCommand command = new PurchaseItemCommand(1L, 1L);
        when(purchaseItemCommandHandler.handle(any(PurchaseItemCommand.class)))
                .thenReturn(CompletableFuture.completedFuture(mockTransferDto));

        // Act
        var mvcResult = mockMvc.perform(post("/api/Transfer/purchase")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(command)))
                .andExpect(request().asyncStarted())
                .andReturn();

        // Assert
        mockMvc.perform(asyncDispatch(mvcResult))
                .andExpect(status().isCreated());
    }

    @Test
    void testPurchaseItem_ItemNotFound() throws Exception {
        // Arrange
        PurchaseItemCommand command = new PurchaseItemCommand(1L, 999L);
        when(purchaseItemCommandHandler.handle(any(PurchaseItemCommand.class)))
                .thenReturn(CompletableFuture.failedFuture(new ResourceNotFoundException("Item not found")));
        
        // Act
        var mvcResult = mockMvc.perform(post("/api/Transfer/purchase")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(command)))
                .andExpect(request().asyncStarted())
                .andReturn();

        // Assert
        mockMvc.perform(asyncDispatch(mvcResult))
                .andExpect(status().isNotFound()); 
    }

    @Test
    void testPurchaseItem_InsufficientBalance() throws Exception {
        // Arrange
        PurchaseItemCommand command = new PurchaseItemCommand(1L, 1L);
        when(purchaseItemCommandHandler.handle(any(PurchaseItemCommand.class)))
                .thenReturn(CompletableFuture.failedFuture(new IllegalArgumentException("Insufficient balance")));
        
        // Act
        var mvcResult = mockMvc.perform(post("/api/Transfer/purchase")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(command)))
                .andExpect(request().asyncStarted())
                .andReturn();

        // Assert
        mockMvc.perform(asyncDispatch(mvcResult))
                .andExpect(status().isBadRequest()); 
    }

    @Test
    void testPurchaseItem_BuyerNotFound() throws Exception {

        // Arrange
        PurchaseItemCommand command = new PurchaseItemCommand(999L, 1L);
        when(purchaseItemCommandHandler.handle(any(PurchaseItemCommand.class)))
                .thenReturn(CompletableFuture.failedFuture(new ResourceNotFoundException("Buyer not found")));
        
        // Act
        var mvcResult = mockMvc.perform(post("/api/Transfer/purchase")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(command)))
                .andExpect(request().asyncStarted())
                .andReturn();

        // Assert
        mockMvc.perform(asyncDispatch(mvcResult))
                .andExpect(status().isNotFound()); 
    }

    @Test
    void testPurchaseItem_ItemNotAvailable() throws Exception {

        // Arrange
        PurchaseItemCommand command = new PurchaseItemCommand(1L, 1L);
        when(purchaseItemCommandHandler.handle(any(PurchaseItemCommand.class)))
                .thenReturn(CompletableFuture.failedFuture(new IllegalArgumentException("Item is not available for purchase")));
        
        // Act
        var mvcResult = mockMvc.perform(post("/api/Transfer/purchase")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(command)))
                .andExpect(request().asyncStarted())
                .andReturn();

        // Assert
        mockMvc.perform(asyncDispatch(mvcResult))
                .andExpect(status().isBadRequest()); 
    }

    @Test
    void testPurchaseItem_ItemDeleted() throws Exception {

        // Arrange
        PurchaseItemCommand command = new PurchaseItemCommand(1L, 1L);
        when(purchaseItemCommandHandler.handle(any(PurchaseItemCommand.class)))
                .thenReturn(CompletableFuture.failedFuture(new IllegalArgumentException("Item has been deleted")));
        
        // Act
        var mvcResult = mockMvc.perform(post("/api/Transfer/purchase")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(command)))
                .andExpect(request().asyncStarted())
                .andReturn();

        // Assert
        mockMvc.perform(asyncDispatch(mvcResult))
                .andExpect(status().isBadRequest()); 
    }

    @Test
    void testPurchaseItem_SellerNotFound() throws Exception {

        // Arrange
        PurchaseItemCommand command = new PurchaseItemCommand(1L, 1L);
        when(purchaseItemCommandHandler.handle(any(PurchaseItemCommand.class)))
                .thenReturn(CompletableFuture.failedFuture(new ResourceNotFoundException("Seller not found")));
        
        // Act
        var mvcResult = mockMvc.perform(post("/api/Transfer/purchase")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(command)))
                .andExpect(request().asyncStarted())
                .andReturn();

        // Assert
        mockMvc.perform(asyncDispatch(mvcResult))
                .andExpect(status().isNotFound()); 
    }

    @Test
    void testPurchaseItem_SameBuyerAndSeller() throws Exception {

        // Arrange
        PurchaseItemCommand command = new PurchaseItemCommand(1L, 1L);
        when(purchaseItemCommandHandler.handle(any(PurchaseItemCommand.class)))
                .thenReturn(CompletableFuture.failedFuture(new IllegalArgumentException("Cannot purchase one's own item")));
        
        // Act
        var mvcResult = mockMvc.perform(post("/api/Transfer/purchase")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(command)))
                .andExpect(request().asyncStarted())
                .andReturn();

        // Assert
        mockMvc.perform(asyncDispatch(mvcResult))
                .andExpect(status().isBadRequest()); 
    }

}
