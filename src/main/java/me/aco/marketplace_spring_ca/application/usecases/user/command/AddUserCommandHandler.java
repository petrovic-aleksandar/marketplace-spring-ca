package me.aco.marketplace_spring_ca.application.usecases.user.command;

import java.util.concurrent.CompletableFuture;


import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import me.aco.marketplace_spring_ca.application.dto.UserDto;
import me.aco.marketplace_spring_ca.domain.entities.User;
import me.aco.marketplace_spring_ca.domain.enums.UserRole;
import me.aco.marketplace_spring_ca.domain.intefrace.PasswordHasher;
import me.aco.marketplace_spring_ca.infrastructure.persistence.JpaUserRepository;

@Service
@Transactional
public class AddUserCommandHandler {

    private final JpaUserRepository userRepository;
    private final PasswordHasher passwordHasher;

    public AddUserCommandHandler(JpaUserRepository userRepository, PasswordHasher passwordHasher) {
        this.userRepository = userRepository;
        this.passwordHasher = passwordHasher;
    }
    
    public CompletableFuture<UserDto> handle(AddUserCommand command) {
        return CompletableFuture.supplyAsync(() -> {
            validateUsername(command.username());
            return new UserDto(userRepository.save(createUser(command)));
        });
    }

    private void validateUsername(String username) {
        if (userRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("Username already exists");
        }
    }

    private User createUser(AddUserCommand command) {
        return new User(
                command.username(),
                passwordHasher.hash(command.password()),
                command.name(),
                command.email(),
                command.phone(),
                UserRole.valueOf(command.role())
        );
    }
}
