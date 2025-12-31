package me.aco.marketplace_spring_ca.application.usecases.user.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import me.aco.marketplace_spring_ca.application.dto.UserDto;
import me.aco.marketplace_spring_ca.application.exceptions.ResourceNotFoundException;
import me.aco.marketplace_spring_ca.domain.entities.User;
import me.aco.marketplace_spring_ca.domain.enums.UserRole;
import me.aco.marketplace_spring_ca.infrastructure.persistence.JpaUserRepository;

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
		UserDto result = handler.handle(command);

        // Assert
		assertTrue(user.isActive());
		assertEquals("testuser", result.username());
	}

	@Test
	void testActivateUserNotFound() {

		// Arrange
		when(userRepository.findById(2L)).thenReturn(Optional.empty());

		// Act & Assert
		ActivateUserCommand command = new ActivateUserCommand(2L);
		ResourceNotFoundException thrown = assertThrows(ResourceNotFoundException.class, () -> handler.handle(command));
		assertTrue(thrown.getMessage().toLowerCase().contains("user not found"));
	}
}
