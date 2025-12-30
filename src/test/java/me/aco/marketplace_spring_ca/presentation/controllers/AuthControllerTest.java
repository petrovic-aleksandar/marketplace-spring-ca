package me.aco.marketplace_spring_ca.presentation.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import me.aco.marketplace_spring_ca.application.dto.TokenDto;
import me.aco.marketplace_spring_ca.application.dto.UserDto;
import me.aco.marketplace_spring_ca.application.usecases.auth.command.LoginCommand;
import me.aco.marketplace_spring_ca.application.usecases.auth.command.LoginCommandHandler;
import me.aco.marketplace_spring_ca.application.usecases.auth.command.RefreshTokenCommand;
import me.aco.marketplace_spring_ca.application.usecases.auth.command.RefreshTokenCommandHandler;
import me.aco.marketplace_spring_ca.application.usecases.auth.command.RegisterCommand;
import me.aco.marketplace_spring_ca.application.usecases.auth.command.RegisterCommandHandler;
import me.aco.marketplace_spring_ca.application.usecases.auth.command.RevokeTokenCommand;
import me.aco.marketplace_spring_ca.application.usecases.auth.command.RevokeTokenCommandHandler;
import me.aco.marketplace_spring_ca.domain.entities.User;
import me.aco.marketplace_spring_ca.domain.enums.UserRole;

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
        when(loginCommandHandler.handle(any(LoginCommand.class))).thenReturn(mockTokenDto);

        // Act & Assert
        mockMvc.perform(post("/api/Auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginCommand)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("access-token-123"))
                .andExpect(jsonPath("$.refreshToken").value("refresh-token-456"));
    }

    @Test
    void testLoginFailure() throws Exception {
        // Arrange
        LoginCommand loginCommand = new LoginCommand("testuser", "wrongpassword");
        when(loginCommandHandler.handle(any(LoginCommand.class)))
                .thenThrow(new me.aco.marketplace_spring_ca.application.exceptions.AuthenticationException("Invalid credentials"));

        // Act & Assert
        mockMvc.perform(post("/api/Auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginCommand)))
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
        when(registerCommandHandler.handle(any(RegisterCommand.class))).thenReturn(mockUserDto);

        // Act & Assert
        mockMvc.perform(post("/api/Auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerCommand)))
                .andExpect(status().isCreated())
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

        when(registerCommandHandler.handle(any(RegisterCommand.class)))
                .thenThrow(new IllegalArgumentException("Username already exists"));

        // Act & Assert
        mockMvc.perform(post("/api/Auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerCommand)))
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
        TokenDto newTokenDto = new TokenDto("new-access-token-789", "new-refresh-token-012");
        when(refreshTokenCommandHandler.handle(any(RefreshTokenCommand.class))).thenReturn(newTokenDto);

        // Act & Assert
        mockMvc.perform(post("/api/Auth/refresh-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(refreshCommand)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("new-access-token-789"))
                .andExpect(jsonPath("$.refreshToken").value("new-refresh-token-012"));
    }

    @Test
    void testRefreshTokenFailure_InvalidToken() throws Exception {
        // Arrange
        RefreshTokenCommand refreshCommand = new RefreshTokenCommand(
                999L,
                "invalid-access-token",
                "invalid-token"
        );
        when(refreshTokenCommandHandler.handle(any(RefreshTokenCommand.class)))
                .thenThrow(new IllegalArgumentException("User not found"));

        // Act & Assert
        mockMvc.perform(post("/api/Auth/refresh-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(refreshCommand)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testRevokeTokenSuccess() throws Exception {
        // Arrange
        RevokeTokenCommand revokeCommand = new RevokeTokenCommand(1L);
        Long userId = 1L;
        when(revokeTokenCommandHandler.handle(any(RevokeTokenCommand.class))).thenReturn(userId);

        // Act & Assert
        mockMvc.perform(post("/api/Auth/revoke-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(revokeCommand)))
                .andExpect(status().isOk())
                .andExpect(content().string("1"));
    }

    @Test
    void testRevokeTokenFailure_InvalidUserId() throws Exception {
        // Arrange
        RevokeTokenCommand revokeCommand = new RevokeTokenCommand(999L);
        when(revokeTokenCommandHandler.handle(any(RevokeTokenCommand.class)))
                .thenThrow(new IllegalArgumentException("User not found"));

        // Act & Assert
        mockMvc.perform(post("/api/Auth/revoke-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(revokeCommand)))
                .andExpect(status().isBadRequest());
    }
}
