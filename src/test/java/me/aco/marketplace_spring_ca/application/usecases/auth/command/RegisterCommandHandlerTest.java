package me.aco.marketplace_spring_ca.application.usecases.auth.command;

import me.aco.marketplace_spring_ca.application.dto.UserDto;
import me.aco.marketplace_spring_ca.domain.entities.User;
import me.aco.marketplace_spring_ca.domain.enums.UserRole;
import me.aco.marketplace_spring_ca.domain.intefrace.PasswordHasher;
import me.aco.marketplace_spring_ca.infrastructure.persistence.JpaUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RegisterCommandHandlerTest {

    @Mock
    private JpaUserRepository userRepository;

    @Mock
    private PasswordHasher passwordHasher;

    @InjectMocks
    private RegisterCommandHandler registerCommandHandler;

    private RegisterCommand validCommand;
    private User mockSavedUser;

    @BeforeEach
    void setUp() {
        validCommand = new RegisterCommand(
                "newuser",
                "password123",
                "newuser@example.com",
                "New User",
                "555-9999"
        );

        mockSavedUser = new User();
        mockSavedUser.setId(1L);
        mockSavedUser.setUsername("newuser");
        mockSavedUser.setPassword("hashedPassword");
        mockSavedUser.setName("New User");
        mockSavedUser.setEmail("newuser@example.com");
        mockSavedUser.setPhone("555-9999");
        mockSavedUser.setBalance(BigDecimal.ZERO);
        mockSavedUser.setRole(UserRole.USER);
        mockSavedUser.setActive(true);
    }

    @Test
    void testRegisterUserSuccess() throws ExecutionException, InterruptedException {
        // Arrange
        when(userRepository.findSingleByUsername("newuser"))
                .thenReturn(Optional.empty());
        when(passwordHasher.hash("password123"))
                .thenReturn("hashedPassword");
        when(userRepository.save(any(User.class)))
                .thenReturn(mockSavedUser);

        // Act
        CompletableFuture<UserDto> result = registerCommandHandler.handle(validCommand);
        UserDto userDto = result.get();

        // Assert
        assertNotNull(userDto, "UserDto should not be null");
        assertEquals("newuser", userDto.username(), "Username should match");
        assertEquals("New User", userDto.name(), "Name should match");
        assertEquals("newuser@example.com", userDto.email(), "Email should match");
        assertEquals("555-9999", userDto.phone(), "Phone should match");

        // Verify interactions
        verify(userRepository, times(1)).findSingleByUsername("newuser");
        verify(passwordHasher, times(1)).hash("password123");
        verify(userRepository, times(1)).save(any(User.class));

        // Verify the saved user had correct properties
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        User savedUser = userCaptor.getValue();
        assertEquals("newuser", savedUser.getUsername(), "Saved user should have correct username");
        assertEquals("hashedPassword", savedUser.getPassword(), "Saved user should have hashed password");
        assertEquals(UserRole.USER, savedUser.getRole(), "Default role should be USER");
        assertTrue(savedUser.isActive(), "User should be active by default");
    }

    @Test
    void testRegisterUserWithExistingUsername() {
        // Arrange
        User existingUser = new User();
        existingUser.setId(999L);
        existingUser.setUsername("newuser");

        when(userRepository.findSingleByUsername("newuser"))
                .thenReturn(Optional.of(existingUser));

        // Act & Assert
        CompletableFuture<UserDto> result = registerCommandHandler.handle(validCommand);

        assertThrows(ExecutionException.class, () -> {
            result.get();
        }, "Should throw ExecutionException when username already exists");

        // Verify the cause is IllegalArgumentException
        try {
            result.get();
        } catch (ExecutionException e) {
            assertTrue(e.getCause() instanceof IllegalArgumentException,
                    "Cause should be IllegalArgumentException");
            assertEquals("Username already exists", e.getCause().getMessage(),
                    "Error message should be 'Username already exists'");
        } catch (InterruptedException e) {
            fail("Should not be interrupted");
        }

        // Verify no user was saved
        verify(userRepository, times(1)).findSingleByUsername("newuser");
        verify(passwordHasher, never()).hash(anyString());
        verify(userRepository, never()).save(any(User.class));
    }
}
