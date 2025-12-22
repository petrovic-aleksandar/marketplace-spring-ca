package me.aco.marketplace_spring_ca.application.usecases.auth.command;

import me.aco.marketplace_spring_ca.application.dto.TokenDto;
import me.aco.marketplace_spring_ca.domain.entities.User;
import me.aco.marketplace_spring_ca.domain.enums.UserRole;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RefreshTokenCommandHandlerTest {

    @Mock
    private JpaUserRepository userRepository;

    @Mock
    private RefreshTokenService refreshTokenService;

    @Mock
    private TokenService tokenService;

    @InjectMocks
    private RefreshTokenCommandHandler refreshTokenCommandHandler;

    private User testUser;
    private RefreshTokenCommand validCommand;

    @BeforeEach
    void setUp() {
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
                "refresh-token-123",
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now()
        );

        validCommand = new RefreshTokenCommand(
                1L,
                "valid-access-token",
                "refresh-token-123"
        );
    }

    @Test
    void testRefreshTokenSuccess() throws ExecutionException, InterruptedException {
        // Arrange
        String newAccessToken = "new-access-token-456";
        String newRefreshToken = "new-refresh-token-789";
        
        when(tokenService.validateTokenIgnoringExpiration("valid-access-token")).thenReturn(true);
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(tokenService.generateToken(testUser)).thenReturn(newAccessToken);
        when(refreshTokenService.generateRefreshToken()).thenReturn(newRefreshToken);
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // Act
        CompletableFuture<TokenDto> result = refreshTokenCommandHandler.handle(validCommand);
        TokenDto tokenDto = result.get();

        // Assert
        assertNotNull(tokenDto, "TokenDto should not be null");
        assertEquals(newAccessToken, tokenDto.accessToken(), "Access token should match");
        assertEquals(newRefreshToken, tokenDto.refreshToken(), "Refresh token should match");
        assertEquals(newRefreshToken, testUser.getRefreshToken(), "User's refresh token should be updated");
        assertNotNull(testUser.getRefreshTokenExpiry(), "Refresh token expiry should be set");
        assertTrue(testUser.getRefreshTokenExpiry().isAfter(LocalDateTime.now()), 
                "Refresh token expiry should be in the future");
        
        verify(tokenService, times(1)).validateTokenIgnoringExpiration("valid-access-token");
        verify(userRepository, times(1)).findById(1L);
        verify(tokenService, times(1)).generateToken(testUser);
        verify(refreshTokenService, times(1)).generateRefreshToken();
        verify(userRepository, times(1)).save(testUser);
    }

    @Test
    void testRefreshTokenFailure_InvalidAccessToken() {
        // Arrange
        when(tokenService.validateTokenIgnoringExpiration("invalid-access-token")).thenReturn(false);
        
        RefreshTokenCommand invalidCommand = new RefreshTokenCommand(
                1L,
                "invalid-access-token",
                "refresh-token-123"
        );

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            refreshTokenCommandHandler.handle(invalidCommand);
        }, "Should throw IllegalArgumentException for invalid access token");

        assertEquals("Invalid access token", exception.getMessage(), 
                "Error message should be 'Invalid access token'");

        verify(tokenService, times(1)).validateTokenIgnoringExpiration("invalid-access-token");
        verify(userRepository, never()).findById(any());
        verify(tokenService, never()).generateToken(any());
        verify(refreshTokenService, never()).generateRefreshToken();
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testRefreshTokenFailure_UserNotFound() {
        // Arrange
        when(tokenService.validateTokenIgnoringExpiration("valid-access-token")).thenReturn(true);
        when(userRepository.findById(999L)).thenReturn(Optional.empty());
        
        RefreshTokenCommand command = new RefreshTokenCommand(
                999L,
                "valid-access-token",
                "refresh-token-123"
        );

        // Act
        CompletableFuture<TokenDto> result = refreshTokenCommandHandler.handle(command);

        // Assert
        assertThrows(ExecutionException.class, () -> {
            result.get();
        }, "Should throw ExecutionException for non-existing user");

        // Verify the cause is IllegalArgumentException
        try {
            result.get();
        } catch (ExecutionException e) {
            assertTrue(e.getCause() instanceof IllegalArgumentException, 
                    "Cause should be IllegalArgumentException");
            assertEquals("User not found", e.getCause().getMessage(), 
                    "Error message should be 'User not found'");
        } catch (InterruptedException e) {
            fail("Should not be interrupted");
        }

        verify(tokenService, times(1)).validateTokenIgnoringExpiration("valid-access-token");
        verify(userRepository, times(1)).findById(999L);
        verify(tokenService, never()).generateToken(any());
        verify(refreshTokenService, never()).generateRefreshToken();
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testRefreshTokenFailure_RefreshTokenMismatch() {
        // Arrange
        when(tokenService.validateTokenIgnoringExpiration("valid-access-token")).thenReturn(true);
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        
        RefreshTokenCommand command = new RefreshTokenCommand(
                1L,
                "valid-access-token",
                "wrong-refresh-token"
        );

        // Act
        CompletableFuture<TokenDto> result = refreshTokenCommandHandler.handle(command);

        // Assert
        assertThrows(ExecutionException.class, () -> {
            result.get();
        }, "Should throw ExecutionException for refresh token mismatch");

        // Verify the cause is IllegalArgumentException
        try {
            result.get();
        } catch (ExecutionException e) {
            assertTrue(e.getCause() instanceof IllegalArgumentException, 
                    "Cause should be IllegalArgumentException");
            assertEquals("Invalid refresh token", e.getCause().getMessage(), 
                    "Error message should be 'Invalid refresh token'");
        } catch (InterruptedException e) {
            fail("Should not be interrupted");
        }

        verify(tokenService, times(1)).validateTokenIgnoringExpiration("valid-access-token");
        verify(userRepository, times(1)).findById(1L);
        verify(tokenService, never()).generateToken(any());
        verify(refreshTokenService, never()).generateRefreshToken();
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testRefreshTokenFailure_RefreshTokenExpired() {
        // Arrange
        testUser.setRefreshTokenExpiry(LocalDateTime.now().minusDays(1)); // Expired
        
        when(tokenService.validateTokenIgnoringExpiration("valid-access-token")).thenReturn(true);
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        // Act
        CompletableFuture<TokenDto> result = refreshTokenCommandHandler.handle(validCommand);

        // Assert
        assertThrows(ExecutionException.class, () -> {
            result.get();
        }, "Should throw ExecutionException for expired refresh token");

        // Verify the cause is IllegalArgumentException
        try {
            result.get();
        } catch (ExecutionException e) {
            assertTrue(e.getCause() instanceof IllegalArgumentException, 
                    "Cause should be IllegalArgumentException");
            assertEquals("Refresh token expired", e.getCause().getMessage(), 
                    "Error message should be 'Refresh token expired'");
        } catch (InterruptedException e) {
            fail("Should not be interrupted");
        }

        verify(tokenService, times(1)).validateTokenIgnoringExpiration("valid-access-token");
        verify(userRepository, times(1)).findById(1L);
        verify(tokenService, never()).generateToken(any());
        verify(refreshTokenService, never()).generateRefreshToken();
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testRefreshTokenFailure_NullRefreshToken() {
        // Arrange
        testUser.setRefreshToken(null);
        
        when(tokenService.validateTokenIgnoringExpiration("valid-access-token")).thenReturn(true);
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        // Act
        CompletableFuture<TokenDto> result = refreshTokenCommandHandler.handle(validCommand);

        // Assert
        assertThrows(ExecutionException.class, () -> {
            result.get();
        }, "Should throw ExecutionException for null refresh token");

        // Verify the cause is IllegalArgumentException
        try {
            result.get();
        } catch (ExecutionException e) {
            assertTrue(e.getCause() instanceof IllegalArgumentException, 
                    "Cause should be IllegalArgumentException");
            assertEquals("Invalid refresh token", e.getCause().getMessage(), 
                    "Error message should be 'Invalid refresh token'");
        } catch (InterruptedException e) {
            fail("Should not be interrupted");
        }

        verify(tokenService, times(1)).validateTokenIgnoringExpiration("valid-access-token");
        verify(userRepository, times(1)).findById(1L);
        verify(tokenService, never()).generateToken(any());
        verify(refreshTokenService, never()).generateRefreshToken();
        verify(userRepository, never()).save(any(User.class));
    }
}
