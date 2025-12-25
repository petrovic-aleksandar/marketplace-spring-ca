package me.aco.marketplace_spring_ca.application.usecases.user.command;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import me.aco.marketplace_spring_ca.application.dto.UserDto;
import me.aco.marketplace_spring_ca.domain.entities.User;
import me.aco.marketplace_spring_ca.domain.enums.UserRole;
import me.aco.marketplace_spring_ca.infrastructure.persistence.JpaUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class UpdateUserCommandHandlerTest {

	@Mock
	private JpaUserRepository userRepository;

	@InjectMocks
	private UpdateUserCommandHandler handler;

	private User existingUser;

	@BeforeEach
	void setUp() {
		existingUser = new User();
		existingUser.setId(1L);
		existingUser.setUsername("olduser");
		existingUser.setPassword("oldpass");
		existingUser.setName("Old Name");
		existingUser.setEmail("old@example.com");
		existingUser.setPhone("555-0000");
		existingUser.setBalance(BigDecimal.ZERO);
		existingUser.setRole(UserRole.USER);
		existingUser.setActive(true);
	}

	@Test
	void testUpdateUserSuccess() throws Exception {
        // Arrange
		UpdateUserCommand command = new UpdateUserCommand(
				1L, 
                "newuser", 
                false, 
                null, 
                "New Name", 
                "new@example.com", 
                "555-1111", 
                "ADMIN");

		when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));
		when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));
        
        // Act
		CompletableFuture<UserDto> resultFuture = handler.handle(command);
		UserDto result = resultFuture.get();
        
		// Assert
		assertEquals("newuser", result.username());
		assertEquals("New Name", result.name());
		assertEquals("new@example.com", result.email());
		assertEquals("555-1111", result.phone());
		assertEquals("ADMIN", result.role());
	}

	@Test
	void testUpdateUserNotFound() {
        // Arrange
		UpdateUserCommand command = new UpdateUserCommand(
				2L, 
                "user", 
                false, 
                null, 
                "Name", 
                "email@example.com", 
                "555-2222", 
                "USER");

		when(userRepository.findById(2L)).thenReturn(Optional.empty());
        
        // Act
		CompletableFuture<UserDto> resultFuture = handler.handle(command);
        
		// Assert
		ExecutionException thrown = assertThrows(ExecutionException.class, resultFuture::get);
		assertTrue(thrown.getCause() instanceof IllegalArgumentException);
		assertTrue(thrown.getCause().getMessage().toLowerCase().contains("user not found"));
	}

	@Test
	void testUpdateUserWithPassword() throws Exception {
        // Arrange
		UpdateUserCommand command = new UpdateUserCommand(
				1L, 
                "user", 
                true, 
                "newpass", 
                "Name", 
                "email@example.com", 
                "555-2222", 
                "USER");

		when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));
		when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        // Act
		CompletableFuture<UserDto> resultFuture = handler.handle(command);
		UserDto result = resultFuture.get();

		// Assert
		assertEquals("newpass", existingUser.getPassword());
		assertEquals("user", result.username());
	}

    @Test
	void testUpdateUserNoPassword() throws Exception {
        // Arrange
		UpdateUserCommand command = new UpdateUserCommand(
				1L, 
                "user", 
                false, 
                "newpass", 
                "Name", 
                "email@example.com", 
                "555-2222", 
                "USER");
                
		when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));
		when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        // Act
		CompletableFuture<UserDto> resultFuture = handler.handle(command);
		UserDto result = resultFuture.get();

		// Assert
		assertEquals("oldpass", existingUser.getPassword());
		assertEquals("user", result.username());
	}
}
