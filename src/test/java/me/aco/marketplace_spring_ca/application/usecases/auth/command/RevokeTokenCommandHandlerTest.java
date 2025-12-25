package me.aco.marketplace_spring_ca.application.usecases.auth.command;

import me.aco.marketplace_spring_ca.domain.entities.User;
import me.aco.marketplace_spring_ca.domain.enums.UserRole;
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
class RevokeTokenCommandHandlerTest {

    @Mock
    private JpaUserRepository userRepository;

    @InjectMocks
    private RevokeTokenCommandHandler revokeTokenCommandHandler;

    private User testUser;

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
    }

    @Test
    void testRevokeTokenSuccess() throws ExecutionException, InterruptedException {

        // Arrange
        RevokeTokenCommand command = new RevokeTokenCommand(1L);
        
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // Act
        CompletableFuture<Long> result = revokeTokenCommandHandler.handle(command);
        Long userId = result.get();

        // Assert
        assertNotNull(userId, "User ID should not be null");
        assertEquals(1L, userId, "User ID should match");
        assertNull(testUser.getRefreshToken(), "Refresh token should be null after revocation");
        
        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).save(testUser);
    }

    @Test
    void testRevokeTokenFailure_UserNotFound() {

        // Arrange
        RevokeTokenCommand command = new RevokeTokenCommand(999L);
        
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        // Act
        CompletableFuture<Long> result = revokeTokenCommandHandler.handle(command);

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

        verify(userRepository, times(1)).findById(999L);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testRevokeTokenAlreadyNull() throws ExecutionException, InterruptedException {
        
        // Arrange
        testUser.setRefreshToken(null); // Already null
        RevokeTokenCommand command = new RevokeTokenCommand(1L);
        
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // Act
        CompletableFuture<Long> result = revokeTokenCommandHandler.handle(command);
        Long userId = result.get();

        // Assert
        assertNotNull(userId, "User ID should not be null");
        assertEquals(1L, userId, "User ID should match");
        assertNull(testUser.getRefreshToken(), "Refresh token should remain null");
        
        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).save(testUser);
    }
}
