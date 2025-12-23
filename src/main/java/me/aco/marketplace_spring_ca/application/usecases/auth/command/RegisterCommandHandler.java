package me.aco.marketplace_spring_ca.application.usecases.auth.command;

import java.util.concurrent.CompletableFuture;

import org.springframework.stereotype.Service;

import me.aco.marketplace_spring_ca.application.dto.UserDto;
import me.aco.marketplace_spring_ca.domain.entities.User;
import me.aco.marketplace_spring_ca.domain.intefrace.PasswordHasher;
import me.aco.marketplace_spring_ca.infrastructure.persistence.JpaUserRepository;

@Service
public class RegisterCommandHandler {

    private final JpaUserRepository userRepository;
    private final PasswordHasher passwordHasher;

    public RegisterCommandHandler(JpaUserRepository userRepository, PasswordHasher passwordHasher) {
        this.userRepository = userRepository;
        this.passwordHasher = passwordHasher;
    }

    public CompletableFuture<UserDto> handle(RegisterCommand command) {
        return CompletableFuture.supplyAsync(() -> {
            if (userRepository.findSingleByUsername(command.username()).isPresent()) {
                throw new IllegalArgumentException("Username already exists");
            }
            return command;
        }).thenCompose(cmd -> {
            User newUser = toUser(cmd);
            return CompletableFuture.supplyAsync(() -> {
                User addedUser = userRepository.save(newUser);
                return new UserDto(addedUser);
            });
        });
    }

    private User toUser(RegisterCommand command) {
		String hashedPassword = passwordHasher.hash(command.password());
		return new User(
			command.username(),
			hashedPassword,
			command.name(),
			command.email(),
			command.phone()
		);
	}
    
}
