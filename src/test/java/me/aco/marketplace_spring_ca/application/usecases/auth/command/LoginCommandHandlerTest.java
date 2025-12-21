package me.aco.marketplace_spring_ca.application.usecases.auth.command;

import me.aco.marketplace_spring_ca.application.dto.TokenDto;
import me.aco.marketplace_spring_ca.application.exceptions.BusinessException;
import me.aco.marketplace_spring_ca.application.exceptions.ResourceNotFoundException;
import me.aco.marketplace_spring_ca.domain.entities.User;
import me.aco.marketplace_spring_ca.domain.enums.UserRole;
import me.aco.marketplace_spring_ca.domain.intefrace.PasswordHasher;
import me.aco.marketplace_spring_ca.domain.intefrace.RefreshTokenService;
import me.aco.marketplace_spring_ca.domain.intefrace.TokenService;
import me.aco.marketplace_spring_ca.infrastructure.persistence.JpaUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoginCommandHandlerTest {

    @Mock
    private JpaUserRepository userRepository;

    @Mock
    private RefreshTokenService refreshTokenService;

    @Mock
    private TokenService tokenService;

    @Mock
    private PasswordHasher passwordHasher;

    @InjectMocks
    private LoginCommandHandler loginCommandHandler;

    private User testUser;
    private LoginCommand validCommand;

    @BeforeEach
    void setUp() {
        // Create a test user
        testUser = new User(
                1L,
                "testuser",
                "hashedPassword123",
                "Test User",
                "test@example.com",
                "555-1234",
                new BigDecimal("100.00"),
                UserRole.USER,
                true,
                null,
                null,
                LocalDateTime.now()
        );

        validCommand = new LoginCommand("testuser", "password123");
    }

    @Test
    void testLoginSuccess() throws ExecutionException, InterruptedException {
        // Arrange
        String expectedToken = "jwt_token_123";
        String expectedRefreshToken = "refresh_token_456";

        when(userRepository.findSingleByUsername("testuser"))
                .thenReturn(Optional.of(testUser));
        when(passwordHasher.verifyPassword("password123", testUser.getPassword()))
                .thenReturn(true);
        when(tokenService.generateToken(testUser))
                .thenReturn(expectedToken);
        when(refreshTokenService.generateRefreshToken())
                .thenReturn(expectedRefreshToken);
        when(userRepository.save(any(User.class)))
                .thenReturn(testUser);

        // Act
        CompletableFuture<TokenDto> result = loginCommandHandler.handle(validCommand);
        TokenDto tokenDto = result.get();

        // Assert
        assertNotNull(tokenDto, "TokenDto should not be null");
        assertEquals(expectedToken, tokenDto.accessToken(), "Access token should match");
        assertEquals(expectedRefreshToken, tokenDto.refreshToken(), "Refresh token should match");
        verify(userRepository, times(1)).findSingleByUsername("testuser");
        verify(passwordHasher, times(1)).verifyPassword("password123", testUser.getPassword());
        verify(tokenService, times(1)).generateToken(testUser);
        verify(refreshTokenService, times(1)).generateRefreshToken();
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testLoginWithWrongPassword() {
        // Arrange
        when(userRepository.findSingleByUsername("testuser"))
                .thenReturn(Optional.of(testUser));
        when(passwordHasher.verifyPassword("wrongPassword", testUser.getPassword()))
                .thenReturn(false);

        LoginCommand wrongPasswordCommand = new LoginCommand("testuser", "wrongPassword");

        // Act & Assert
        CompletableFuture<TokenDto> result = loginCommandHandler.handle(wrongPasswordCommand);
        
        assertThrows(ExecutionException.class, () -> {
            result.get();
        }, "Should throw ExecutionException for wrong password");

        // Verify the cause is BusinessException
        try {
            result.get();
        } catch (ExecutionException e) {
            assertTrue(e.getCause() instanceof BusinessException, 
                    "Cause should be BusinessException");
            assertEquals("Invalid credentials", e.getCause().getMessage(), 
                    "Error message should be 'Invalid credentials'");
        } catch (InterruptedException e) {
            fail("Should not be interrupted");
        }

        verify(userRepository, times(1)).findSingleByUsername("testuser");
        verify(passwordHasher, times(1)).verifyPassword("wrongPassword", testUser.getPassword());
        verify(tokenService, never()).generateToken(any());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testLoginWithNonExistingUser() {
        // Arrange
        when(userRepository.findSingleByUsername("nonexistentuser"))
                .thenReturn(Optional.empty());

        LoginCommand nonExistentCommand = new LoginCommand("nonexistentuser", "password123");

        // Act & Assert
        CompletableFuture<TokenDto> result = loginCommandHandler.handle(nonExistentCommand);
        
        assertThrows(ExecutionException.class, () -> {
            result.get();
        }, "Should throw ExecutionException for non-existing user");

        // Verify the cause is ResourceNotFoundException
        try {
            result.get();
        } catch (ExecutionException e) {
            assertTrue(e.getCause() instanceof ResourceNotFoundException, 
                    "Cause should be ResourceNotFoundException");
            assertEquals("User not found", e.getCause().getMessage(), 
                    "Error message should be 'User not found'");
        } catch (InterruptedException e) {
            fail("Should not be interrupted");
        }

        verify(userRepository, times(1)).findSingleByUsername("nonexistentuser");
        verify(passwordHasher, never()).verifyPassword(anyString(), anyString());
        verify(tokenService, never()).generateToken(any());
        verify(userRepository, never()).save(any(User.class));
    }
}