package me.aco.marketplace_spring_ca.application.usecases.auth.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import me.aco.marketplace_spring_ca.application.dto.TokenDto;
import me.aco.marketplace_spring_ca.application.exceptions.BusinessException;
import me.aco.marketplace_spring_ca.application.exceptions.ResourceNotFoundException;
import me.aco.marketplace_spring_ca.domain.entities.User;
import me.aco.marketplace_spring_ca.domain.enums.UserRole;
import me.aco.marketplace_spring_ca.domain.intefrace.RefreshTokenService;
import me.aco.marketplace_spring_ca.domain.intefrace.TokenService;
import me.aco.marketplace_spring_ca.infrastructure.persistence.JpaUserRepository;

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
    private String newAccessToken = "new-access-token-456";
    private String newRefreshToken = "new-refresh-token-789";

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
    void testRefreshTokenSuccess() {

        // Arrange
        when(tokenService.validateTokenIgnoringExpiration("valid-access-token")).thenReturn(true);
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(tokenService.generateToken(testUser)).thenReturn(newAccessToken);
        when(refreshTokenService.generateRefreshToken()).thenReturn(newRefreshToken);
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        TokenDto tokenDto = refreshTokenCommandHandler.handle(validCommand);

        // Assert
        assertNotNull(tokenDto, "TokenDto should not be null");
        assertEquals(newAccessToken, tokenDto.accessToken(), "Access token should match");
        assertEquals(newRefreshToken, tokenDto.refreshToken(), "Refresh token should match");
        assertEquals(newRefreshToken, testUser.getRefreshToken(), "User's refresh token should be updated");
        assertNotNull(testUser.getRefreshTokenExpiry(), "Refresh token expiry should be set");
        assertTrue(testUser.getRefreshTokenExpiry().isAfter(LocalDateTime.now()), "Refresh token expiry should be in the future");
        verify(tokenService, times(1)).validateTokenIgnoringExpiration("valid-access-token");
        verify(userRepository, times(1)).findById(1L);
        verify(tokenService, times(1)).generateToken(testUser);
        verify(refreshTokenService, times(1)).generateRefreshToken();
        verify(userRepository, times(1)).save(testUser);
    }

    @Test
    void testRefreshTokenFailure_UserNotFound() {

        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException thrown = assertThrows(ResourceNotFoundException.class, () -> {
            refreshTokenCommandHandler.handle(validCommand);
        }, "Should throw ResourceNotFoundException for non-existing user");
        assertEquals("User not found", thrown.getMessage(), "Error message should be 'User not found'");
    }

    @Test
    void testRefreshTokenFailure_InvalidAccessToken() {

        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(tokenService.validateTokenIgnoringExpiration("invalid-access-token")).thenReturn(false);
        RefreshTokenCommand invalidAccessCommand = new RefreshTokenCommand(
                1L,
                "invalid-access-token",
                "refresh-token-123"
        );
        // Act & Assert
        BusinessException thrown = assertThrows(BusinessException.class, () -> {
            refreshTokenCommandHandler.handle(invalidAccessCommand);
        }, "Should throw BusinessException for invalid access token");
        assertEquals("Invalid access token", thrown.getMessage(), "Error message should be 'Invalid access token'");
    }

    @Test
    void testRefreshTokenFailure_InvalidRefreshToken() {

        // Arrange
        when(tokenService.validateTokenIgnoringExpiration("valid-access-token")).thenReturn(true);
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(tokenService.isTokenExpired("valid-access-token")).thenReturn(true);
        
        RefreshTokenCommand invalidRefreshCommand = new RefreshTokenCommand(
                1L,
                "valid-access-token",
                "invalid-refresh-token"
        );

        // Act & Assert
        BusinessException thrown = assertThrows(BusinessException.class, () -> {
            refreshTokenCommandHandler.handle(invalidRefreshCommand);
        }, "Should throw BusinessException for invalid refresh token");
        assertEquals("Invalid refresh token", thrown.getMessage(), "Error message should be 'Invalid refresh token'");
    }

    @Test
    void testRefreshTokenFailure_ExpiredRefreshToken() {

        // Arrange
        testUser.setRefreshTokenExpiry(LocalDateTime.now().minusDays(1)); // Expire the token
        when(tokenService.validateTokenIgnoringExpiration("valid-access-token")).thenReturn(true);
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(tokenService.isTokenExpired("valid-access-token")).thenReturn(true);

        // Act & Assert
        BusinessException thrown = assertThrows(BusinessException.class, () -> {
            refreshTokenCommandHandler.handle(validCommand);
        }, "Should throw BusinessException for expired refresh token");
        assertEquals("Refresh token expired", thrown.getMessage(), "Error message should be 'Refresh token expired'");
    }

    @Test
    void testRefreshTokenFailure_NullAccessToken() {

        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        RefreshTokenCommand nullAccessTokenCommand = new RefreshTokenCommand(
                1L,
                null,
                "refresh-token-123"
        );

        // Act & Assert
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            refreshTokenCommandHandler.handle(nullAccessTokenCommand);
        }, "Should throw IllegalArgumentException for null access token");
        assertEquals("Access token cannot be null or empty", thrown.getMessage(), "Error message should be 'Access token cannot be null or empty'");
    }

    @Test
    void testRefreshTokenFailure_NullRefreshToken() {

        // Arrange
        when(tokenService.validateTokenIgnoringExpiration("valid-access-token")).thenReturn(true);
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(tokenService.isTokenExpired("valid-access-token")).thenReturn(true);
        RefreshTokenCommand nullRefreshTokenCommand = new RefreshTokenCommand(
                1L,
                "valid-access-token",
                null
        );

        // Act & Assert
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            refreshTokenCommandHandler.handle(nullRefreshTokenCommand);
        }, "Should throw IllegalArgumentException for null refresh token");
        assertEquals("Refresh token cannot be null or empty", thrown.getMessage(), "Error message should be 'Refresh token cannot be null or empty'");
    }

    @Test
    void testRefreshTokenFailure_EmptyAccessToken() {

        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        RefreshTokenCommand emptyAccessTokenCommand = new RefreshTokenCommand(
                1L,
                "",
                "refresh-token-123"
        );

        // Act & Assert
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            refreshTokenCommandHandler.handle(emptyAccessTokenCommand);
        }, "Should throw IllegalArgumentException for empty access token");
        assertEquals("Access token cannot be null or empty", thrown.getMessage(), "Error message should be 'Access token cannot be null or empty'");
    }

    @Test
    void testRefreshTokenFailure_EmptyRefreshToken() {

        // Arrange
        when(tokenService.validateTokenIgnoringExpiration("valid-access-token")).thenReturn(true);
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(tokenService.isTokenExpired("valid-access-token")).thenReturn(true);
        RefreshTokenCommand emptyRefreshTokenCommand = new RefreshTokenCommand(
                1L,
                "valid-access-token",
                ""
        );
        

        // Act & Assert
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            refreshTokenCommandHandler.handle(emptyRefreshTokenCommand);
        }, "Should throw IllegalArgumentException for empty refresh token");
        assertEquals("Refresh token cannot be null or empty", thrown.getMessage(), "Error message should be 'Refresh token cannot be null or empty'");
    }

    @Test
    void testRefreshTokenFailure_NullUserId() {

        // Arrange
        RefreshTokenCommand nullUserIdCommand = new RefreshTokenCommand(
                null,
                "valid-access-token",
                "refresh-token-123"
        );

        // Act & Assert
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            refreshTokenCommandHandler.handle(nullUserIdCommand);
        }, "Should throw IllegalArgumentException for null user ID");
        assertEquals("User ID cannot be null", thrown.getMessage(), "Error message should be 'User ID cannot be null'");
    }


}