package me.aco.marketplace_spring_ca.application.usecases.user.command;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.util.Optional;
// ...existing code...

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
public class DeactivateUserCommandHandlerTest {

	@Mock
	private JpaUserRepository userRepository;

	@InjectMocks
	private DeactivateUserCommandHandler handler;

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
		user.setActive(true);
	}

	@Test
	void testDeactivateUserSuccess() throws Exception {
        // Arrange
		when(userRepository.findById(1L))
            .thenReturn(Optional.of(user));
		when(userRepository.save(any(User.class)))
            .thenAnswer(inv -> inv.getArgument(0));

		// Act
		DeactivateUserCommand command = new DeactivateUserCommand(1L);
		UserDto result = handler.handle(command);

        // Assert
		assertFalse(user.isActive());
		assertEquals("testuser", result.username());
	}

	@Test
	void testDeactivateUserNotFound() {
		// Arrange
		when(userRepository.findById(2L)).thenReturn(Optional.empty());

		// Act & Assert
		DeactivateUserCommand command = new DeactivateUserCommand(2L);
		ResourceNotFoundException thrown = assertThrows(ResourceNotFoundException.class, () -> handler.handle(command));
		assertTrue(thrown.getMessage().toLowerCase().contains("user not found"));
	}
}
