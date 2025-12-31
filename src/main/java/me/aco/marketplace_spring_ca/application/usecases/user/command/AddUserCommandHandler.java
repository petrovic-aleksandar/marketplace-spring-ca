package me.aco.marketplace_spring_ca.application.usecases.user.command;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import me.aco.marketplace_spring_ca.application.dto.UserDto;
import me.aco.marketplace_spring_ca.domain.entities.User;
import me.aco.marketplace_spring_ca.domain.enums.UserRole;
import me.aco.marketplace_spring_ca.domain.intefrace.PasswordHasher;
import me.aco.marketplace_spring_ca.infrastructure.persistence.JpaUserRepository;

@Service
@Transactional
@RequiredArgsConstructor
public class AddUserCommandHandler {

    private final JpaUserRepository userRepository;
    private final PasswordHasher passwordHasher;

    public UserDto handle(AddUserCommand command) {

        validateCommand(command);

        checkIfUsernameExists(command.username());
        return new UserDto(userRepository.save(createUser(command)));
    }

    private void validateCommand(AddUserCommand command) {
        if (command.username() == null || command.username().isBlank())
            throw new IllegalArgumentException("Username cannot be null or blank");

        if (command.password() == null || command.password().isBlank())
            throw new IllegalArgumentException("Password cannot be null or blank");

        if (command.name() == null || command.name().isBlank())
            throw new IllegalArgumentException("Name cannot be null or blank");

        if (command.email() == null || command.email().isBlank())
            throw new IllegalArgumentException("Email cannot be null or blank");

        if (command.phone() == null || command.phone().isBlank())
            throw new IllegalArgumentException("Phone cannot be null or blank");

        if (command.role() == null || command.role().isBlank())
            throw new IllegalArgumentException("Role cannot be null or blank");

        try {
            UserRole.valueOf(command.role());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid role: " + command.role());
        }
    }

    private void checkIfUsernameExists(String username) {
        if (userRepository.existsByUsername(username))
            throw new IllegalArgumentException("Username already exists");
    }

    private User createUser(AddUserCommand command) {
        return new User(
                command.username(),
                passwordHasher.hash(command.password()),
                command.name(),
                command.email(),
                command.phone(),
                UserRole.valueOf(command.role()));
    }
}
