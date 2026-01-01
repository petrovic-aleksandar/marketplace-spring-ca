package me.aco.marketplace_spring_ca.application.usecases.auth.command;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import me.aco.marketplace_spring_ca.application.dto.UserDto;
import me.aco.marketplace_spring_ca.domain.entities.User;
import me.aco.marketplace_spring_ca.domain.intefrace.PasswordHasher;
import me.aco.marketplace_spring_ca.infrastructure.persistence.JpaUserRepository;

@Service
@Transactional
@RequiredArgsConstructor
public class UpdateSelfCommandHandler {

    private final JpaUserRepository userRepository;
    private final PasswordHasher passwordHasher;

    public UserDto handle(UpdateSelfCommand command) {

        validateCommand(command);

        User user = fetchUser(command.id());

        user = updateUser(user, command);
        user = save(user);
        return new UserDto(user);
    }

    private void validateCommand(UpdateSelfCommand command) {

        if (command.id() == null)
            throw new IllegalArgumentException("User ID cannot be null");

        if (command.username() == null || command.username().isBlank())
            throw new IllegalArgumentException("Username cannot be null or blank");

        if (command.updatePassword() && (command.password() == null || command.password().isBlank()))
            throw new IllegalArgumentException("Password cannot be null or blank when updating password");

        if (command.name() == null || command.name().isBlank())
            throw new IllegalArgumentException("Name cannot be null or blank");

        if (command.email() == null || command.email().isBlank())
            throw new IllegalArgumentException("Email cannot be null or blank");

        if (command.phone() == null || command.phone().isBlank())
            throw new IllegalArgumentException("Phone cannot be null or blank");
    }

    private User fetchUser(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }

    private User updateUser(User user, UpdateSelfCommand command) {
        user.setUsername(command.username());
        if (command.updatePassword()) {
            user.setPassword(passwordHasher.hash(command.password()));
        }
        user.setName(command.name());
        user.setEmail(command.email());
        user.setPhone(command.phone());
        return user;
    }

    private User save(User user) {
        return userRepository.save(user);
    }

}
