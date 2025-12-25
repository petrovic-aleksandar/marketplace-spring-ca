package me.aco.marketplace_spring_ca.application.usecases.user.command;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import me.aco.marketplace_spring_ca.application.dto.UserDto;
import me.aco.marketplace_spring_ca.application.exceptions.ResourceNotFoundException;
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
public class ActivateUserCommandHandlerTest {

	@Mock
	private JpaUserRepository userRepository;

	@InjectMocks
	private ActivateUserCommandHandler handler;

	private User user;

	@BeforeEach
	void setUp() {
		user = new User();
		user.setId(1L);
		user.setUsername("testuser");
		user.setPassword("password");
		user.setName("Test User");
		user.setEmail("test@example.com");
		user.setPhone("555-1234");
		user.setBalance(BigDecimal.ZERO);
		user.setRole(UserRole.USER);
		user.setActive(false);
	}

	@Test
	void testActivateUserSuccess() throws Exception {

        // Arrange
		when(userRepository.findById(1L)).thenReturn(Optional.of(user));
		when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        // Act
		ActivateUserCommand command = new ActivateUserCommand(1L);
		CompletableFuture<UserDto> resultFuture = handler.handle(command);
		UserDto result = resultFuture.get();

        // Assert
		assertTrue(user.isActive());
		assertEquals("testuser", result.username());
	}

	@Test
	void testActivateUserNotFound() {

        // Arrange
		when(userRepository.findById(2L)).thenReturn(Optional.empty());

        // Act
		ActivateUserCommand command = new ActivateUserCommand(2L);
		CompletableFuture<UserDto> resultFuture = handler.handle(command);
		ExecutionException thrown = assertThrows(ExecutionException.class, resultFuture::get);

        // Assert
		assertTrue(thrown.getCause() instanceof ResourceNotFoundException);
		assertTrue(thrown.getCause().getMessage().toLowerCase().contains("user not found"));
	}
}
