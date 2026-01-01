package me.aco.marketplace_spring_ca.application.usecases.auth.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
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
import me.aco.marketplace_spring_ca.application.exceptions.AuthenticationException;
import me.aco.marketplace_spring_ca.domain.entities.User;
import me.aco.marketplace_spring_ca.domain.enums.UserRole;
import me.aco.marketplace_spring_ca.domain.intefrace.PasswordHasher;
import me.aco.marketplace_spring_ca.domain.intefrace.RefreshTokenService;
import me.aco.marketplace_spring_ca.domain.intefrace.TokenService;
import me.aco.marketplace_spring_ca.infrastructure.persistence.JpaUserRepository;

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
    private String accessToken = "jwt_token_123";
    private String refreshToken = "refresh_token_456";

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
                LocalDateTime.now());

        validCommand = new LoginCommand("testuser", "password123");
    }

    @Test
    void testLoginSuccess() {

        // Arrange
        when(userRepository.findSingleByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(passwordHasher.verify("password123", testUser.getPassword())).thenReturn(true);
        when(tokenService.generateToken(testUser)).thenReturn(accessToken);
        when(refreshTokenService.generateRefreshToken()).thenReturn(refreshToken);
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        TokenDto tokenDto = loginCommandHandler.handle(validCommand);

        // Assert
        assertNotNull(tokenDto);
        assertEquals(accessToken, tokenDto.accessToken());
        assertEquals(refreshToken, tokenDto.refreshToken());
        verify(userRepository, times(1)).findSingleByUsername("testuser");
        verify(passwordHasher, times(1)).verify("password123", testUser.getPassword());
        verify(tokenService, times(1)).generateToken(testUser);
        verify(refreshTokenService, times(1)).generateRefreshToken();
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testLoginFailure_WrongPassword() {

        // Arrange
        when(userRepository.findSingleByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(passwordHasher.verify("wrongPassword", testUser.getPassword())).thenReturn(false);
        LoginCommand wrongPasswordCommand = new LoginCommand("testuser", "wrongPassword");

        // Act & Assert
        assertThrows(AuthenticationException.class, () -> {
            loginCommandHandler.handle(wrongPasswordCommand);
        });

        verify(userRepository, times(1)).findSingleByUsername("testuser");
        verify(passwordHasher, times(1)).verify("wrongPassword", testUser.getPassword());
        verify(tokenService, never()).generateToken(any());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testLoginFailure_NonExistingUser() {

        // Arrange
        when(userRepository.findSingleByUsername("nonexistentuser")).thenReturn(Optional.empty());
        LoginCommand nonExistentCommand = new LoginCommand("nonexistentuser", "password123");

        // Act & Assert
        assertThrows(AuthenticationException.class, () -> {
            loginCommandHandler.handle(nonExistentCommand);
        });

        verify(userRepository, times(1)).findSingleByUsername("nonexistentuser");
        verify(passwordHasher, never()).verify(anyString(), anyString());
        verify(tokenService, never()).generateToken(any());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testLoginFailure_InvalidCommandEmptyPassword() {

        // Arrange
        LoginCommand invalidCommand = new LoginCommand("okusername", "");

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            loginCommandHandler.handle(invalidCommand);
        });

        verify(userRepository, never()).findSingleByUsername(anyString());
        verify(passwordHasher, never()).verify(anyString(), anyString());
        verify(tokenService, never()).generateToken(any());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testLoginFailure_InvalidCommandEmptyUsername() {
        
        // Arrange
        LoginCommand invalidCommand = new LoginCommand("", "okpassword");

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            loginCommandHandler.handle(invalidCommand);
        });

        verify(userRepository, never()).findSingleByUsername(anyString());
        verify(passwordHasher, never()).verify(anyString(), anyString());
        verify(tokenService, never()).generateToken(any());
        verify(userRepository, never()).save(any(User.class));
    }
}