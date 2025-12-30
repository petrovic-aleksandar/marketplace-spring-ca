package me.aco.marketplace_spring_ca.application.usecases.auth.command;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import me.aco.marketplace_spring_ca.application.dto.UserDto;
import me.aco.marketplace_spring_ca.application.exceptions.BusinessException;
import me.aco.marketplace_spring_ca.domain.entities.User;
import me.aco.marketplace_spring_ca.domain.intefrace.PasswordHasher;
import me.aco.marketplace_spring_ca.infrastructure.persistence.JpaUserRepository;

@Service
@Transactional
@RequiredArgsConstructor
public class RegisterCommandHandler {

    private final JpaUserRepository userRepository;
    private final PasswordHasher passwordHasher;

    public UserDto handle(RegisterCommand command) {
        validateCommand(command);
        checkIfUsernameExists(command.username());
        return new UserDto(saveUser(toUser(command)));
    }

    private void validateCommand(RegisterCommand command) {
        if (command.username() == null || command.username().isEmpty())
            throw new BusinessException("Username cannot be empty");
        if (command.password() == null || command.password().isEmpty())
            throw new BusinessException("Password cannot be empty");
        if (command.email() == null || command.email().isEmpty())
            throw new BusinessException("Email cannot be empty");
        if (command.name() == null || command.name().isEmpty())
            throw new BusinessException("Name cannot be empty");
        if (command.phone() == null || command.phone().isEmpty())
            throw new BusinessException("Phone cannot be empty");
    }

    private void checkIfUsernameExists(String username) {
        if (userRepository.findSingleByUsername(username).isPresent())
            throw new BusinessException("Username is already taken");
    }

    private User toUser(RegisterCommand command) {
        String hashedPassword = passwordHasher.hash(command.password());
        return new User(
                command.username(),
                hashedPassword,
                command.name(),
                command.email(),
                command.phone());
    }

    private User saveUser(User user) {
        return userRepository.save(user);
    }

}
