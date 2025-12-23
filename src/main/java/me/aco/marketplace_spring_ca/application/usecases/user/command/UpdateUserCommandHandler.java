package me.aco.marketplace_spring_ca.application.usecases.user.command;

import java.util.concurrent.CompletableFuture;

import org.springframework.stereotype.Service;

import me.aco.marketplace_spring_ca.application.dto.UserDto;
import me.aco.marketplace_spring_ca.domain.enums.UserRole;
import me.aco.marketplace_spring_ca.infrastructure.persistence.JpaUserRepository;

@Service
public class UpdateUserCommandHandler {

    private final JpaUserRepository userRepository;

    public UpdateUserCommandHandler(JpaUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public CompletableFuture<UserDto> handle(UpdateUserCommand command) {
        return CompletableFuture.supplyAsync(() -> {
            var user = userRepository.findById(command.id())
                    .orElseThrow(() -> new IllegalArgumentException("User not found"));

            user.setUsername(command.username());
            if (command.updatePassword()) {
                user.setPassword(command.password());
            }
            user.setName(command.name());
            user.setEmail(command.email());
            user.setPhone(command.phone());
            user.setRole(UserRole.valueOf(command.role()));
            
            return new UserDto(userRepository.save(user));
        });
    }
    
}
