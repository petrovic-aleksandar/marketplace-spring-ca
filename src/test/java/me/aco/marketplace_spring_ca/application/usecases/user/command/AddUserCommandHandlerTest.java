package me.aco.marketplace_spring_ca.application.usecases.user.command;

import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import me.aco.marketplace_spring_ca.application.dto.UserDto;
import me.aco.marketplace_spring_ca.domain.entities.User;
import me.aco.marketplace_spring_ca.domain.enums.UserRole;
import me.aco.marketplace_spring_ca.domain.intefrace.PasswordHasher;
import me.aco.marketplace_spring_ca.infrastructure.persistence.JpaUserRepository;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;

@ExtendWith(MockitoExtension.class)
public class AddUserCommandHandlerTest {

    @Mock
    private JpaUserRepository userRepository;
    @Mock
    private PasswordHasher passwordHasher;

    @InjectMocks
    private AddUserCommandHandler addUserCommandHandler;

    private AddUserCommand validCommand;
    private User mockSavedUser;

    @BeforeEach
    void setUp() {

        validCommand = new AddUserCommand(
                "testuser",
                "testpassword",
                "Test User",
                "testuser@example.com",
                "555-1234",
                "USER"
        );

        mockSavedUser = new User();
        mockSavedUser.setId(1L);
        mockSavedUser.setUsername("testuser");
        mockSavedUser.setPassword("hashedPassword");
        mockSavedUser.setName("Test User");
        mockSavedUser.setEmail("testuser@example.com");
        mockSavedUser.setPhone("555-1234");
        mockSavedUser.setBalance(BigDecimal.ZERO);
        mockSavedUser.setRole(UserRole.USER);
        mockSavedUser.setActive(true);
    }

    @Test
    void testHandleValidCommand() throws ExecutionException, InterruptedException {

        //Arrange
        when(passwordHasher.hash(anyString()))
            .thenReturn("hashedPassword");
        when(userRepository.save(any()))
            .thenReturn(mockSavedUser);

        //Act
        CompletableFuture<UserDto> resultFuture = addUserCommandHandler.handle(validCommand);
        UserDto result = resultFuture.get();

        //Assert
        assertNotNull(result);
        assertEquals(1L, result.id());
        assertEquals("testuser", result.username());
        assertEquals("Test User", result.name());
        assertEquals("testuser@example.com", result.email());
        assertEquals("555-1234", result.phone());
        assertEquals(BigDecimal.ZERO, result.balance());
        assertEquals("USER", result.role());
        assertTrue(result.active());
    }

    @Test
    void testHandleUsernameAlreadyExists() throws ExecutionException, InterruptedException {

        // Arrange
        when(userRepository.existsByUsername("testuser")).thenReturn(true);

        // Act
        CompletableFuture<UserDto> resultFuture = addUserCommandHandler.handle(validCommand);

        // Assert
        ExecutionException thrown = assertThrows(ExecutionException.class, resultFuture::get);
        assertTrue(thrown.getCause() instanceof IllegalArgumentException);
        assertTrue(thrown.getCause().getMessage().toLowerCase().contains("username"));
    }

    
}
