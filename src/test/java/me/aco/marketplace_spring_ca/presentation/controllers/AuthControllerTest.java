package me.aco.marketplace_spring_ca.presentation.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.aco.marketplace_spring_ca.application.dto.TokenDto;
import me.aco.marketplace_spring_ca.application.dto.UserDto;
import me.aco.marketplace_spring_ca.application.usecases.auth.command.*;
import me.aco.marketplace_spring_ca.domain.entities.User;
import me.aco.marketplace_spring_ca.domain.enums.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.concurrent.CompletableFuture;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.asyncDispatch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.request;

@WebMvcTest(AuthController.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private LoginCommandHandler loginCommandHandler;

    @MockitoBean
    private RegisterCommandHandler registerCommandHandler;

    @MockitoBean
    private RefreshTokenCommandHandler refreshTokenCommandHandler;

    @MockitoBean
    private RevokeTokenCommandHandler revokeTokenCommandHandler;

    private TokenDto mockTokenDto;
    private UserDto mockUserDto;

    @BeforeEach
    void setUp() {
        mockTokenDto = new TokenDto("access-token-123", "refresh-token-456");

        User mockUser = new User(
                1L,
                "testuser",
                "hashedPassword",
                "Test User",
                "test@example.com",
                "555-1234",
                BigDecimal.ZERO,
                UserRole.USER,
                true,
                null,
                null,
                null
        );
        
        mockUserDto = new UserDto(mockUser);
    }

    @Test
    void testPingEndpoint() throws Exception {
        mockMvc.perform(get("/api/Auth/ping"))
                .andExpect(status().isOk())
                .andExpect(content().string("pong"));
    }

    @Test
    void testLoginSuccess() throws Exception {
        // Arrange
        LoginCommand loginCommand = new LoginCommand("testuser", "password123");
        
        when(loginCommandHandler.handle(any(LoginCommand.class)))
                .thenReturn(CompletableFuture.completedFuture(mockTokenDto));

        // Act & Assert
        mockMvc.perform(post("/api/Auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginCommand)))
                .andExpect(request().asyncStarted())
                .andDo(result -> mockMvc.perform(asyncDispatch(result)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("access-token-123"))
                .andExpect(jsonPath("$.refreshToken").value("refresh-token-456"));
    }

    @Test
    void testLoginFailure() throws Exception {
        // Arrange
        LoginCommand loginCommand = new LoginCommand("testuser", "wrongpassword");
        
        CompletableFuture<TokenDto> failedFuture = new CompletableFuture<>();
        failedFuture.completeExceptionally(new IllegalArgumentException("Invalid credentials"));
        
        when(loginCommandHandler.handle(any(LoginCommand.class)))
                .thenReturn(failedFuture);

        // Act & Assert
        mockMvc.perform(post("/api/Auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginCommand)))
                .andExpect(request().asyncStarted())
                .andDo(result -> mockMvc.perform(asyncDispatch(result)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testRegisterSuccess() throws Exception {
        // Arrange
        RegisterCommand registerCommand = new RegisterCommand(
                "newuser",
                "password123",
                "newuser@example.com",
                "New User",
                "555-9999"
        );
        
        when(registerCommandHandler.handle(any(RegisterCommand.class)))
                .thenReturn(CompletableFuture.completedFuture(mockUserDto));

        // Act & Assert
        mockMvc.perform(post("/api/Auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerCommand)))
                .andExpect(request().asyncStarted())
                .andDo(result -> mockMvc.perform(asyncDispatch(result)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andExpect(jsonPath("$.name").value("Test User"))
                .andExpect(jsonPath("$.phone").value("555-1234"));
    }

    @Test
    void testRegisterFailure_UsernameExists() throws Exception {
        // Arrange
        RegisterCommand registerCommand = new RegisterCommand(
                "existinguser",
                "password123",
                "existing@example.com",
                "Existing User",
                "555-8888"
        );
        
        CompletableFuture<UserDto> failedFuture = new CompletableFuture<>();
        failedFuture.completeExceptionally(new IllegalArgumentException("Username already exists"));
        
        when(registerCommandHandler.handle(any(RegisterCommand.class)))
                .thenReturn(failedFuture);

        // Act & Assert
        mockMvc.perform(post("/api/Auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerCommand)))
                .andExpect(request().asyncStarted())
                .andDo(result -> mockMvc.perform(asyncDispatch(result)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testRefreshTokenSuccess() throws Exception {
        // Arrange
        RefreshTokenCommand refreshCommand = new RefreshTokenCommand(
                1L,
                "access-token-123",
                "refresh-token-456"
        );
        Long userId = 1L;
        
        when(refreshTokenCommandHandler.handle(any(RefreshTokenCommand.class)))
                .thenReturn(CompletableFuture.completedFuture(userId));

        // Act & Assert
        mockMvc.perform(post("/api/Auth/refresh-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(refreshCommand)))
                .andExpect(request().asyncStarted())
                .andDo(result -> mockMvc.perform(asyncDispatch(result)))
                .andExpect(status().isOk())
                .andExpect(content().string("1"));
    }

    @Test
    void testRefreshTokenFailure_InvalidToken() throws Exception {
        // Arrange
        RefreshTokenCommand refreshCommand = new RefreshTokenCommand(
                999L,
                "invalid-access-token",
                "invalid-token"
        );
        
        CompletableFuture<Long> failedFuture = new CompletableFuture<>();
        failedFuture.completeExceptionally(new IllegalArgumentException("User not found"));
        
        when(refreshTokenCommandHandler.handle(any(RefreshTokenCommand.class)))
                .thenReturn(failedFuture);

        // Act & Assert
        mockMvc.perform(post("/api/Auth/refresh-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(refreshCommand)))
                .andExpect(request().asyncStarted())
                .andDo(result -> mockMvc.perform(asyncDispatch(result)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testRevokeTokenSuccess() throws Exception {
        // Arrange
        RevokeTokenCommand revokeCommand = new RevokeTokenCommand(1L);
        Long userId = 1L;
        
        when(revokeTokenCommandHandler.handle(any(RevokeTokenCommand.class)))
                .thenReturn(CompletableFuture.completedFuture(userId));

        // Act & Assert
        mockMvc.perform(post("/api/Auth/revoke-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(revokeCommand)))
                .andExpect(request().asyncStarted())
                .andDo(result -> mockMvc.perform(asyncDispatch(result)))
                .andExpect(status().isOk())
                .andExpect(content().string("1"));
    }

    @Test
    void testRevokeTokenFailure_InvalidUserId() throws Exception {
        // Arrange
        RevokeTokenCommand revokeCommand = new RevokeTokenCommand(999L);
        
        CompletableFuture<Long> failedFuture = new CompletableFuture<>();
        failedFuture.completeExceptionally(new IllegalArgumentException("User not found"));
        
        when(revokeTokenCommandHandler.handle(any(RevokeTokenCommand.class)))
                .thenReturn(failedFuture);

        // Act & Assert
        mockMvc.perform(post("/api/Auth/revoke-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(revokeCommand)))
                .andExpect(request().asyncStarted())
                .andDo(result -> mockMvc.perform(asyncDispatch(result)))
                .andExpect(status().isUnauthorized());
    }
}
