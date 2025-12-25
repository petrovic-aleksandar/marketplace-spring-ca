package me.aco.marketplace_spring_ca.application.usecases.user.command;

import java.util.concurrent.CompletableFuture;


import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import me.aco.marketplace_spring_ca.application.dto.UserDto;
import me.aco.marketplace_spring_ca.domain.enums.UserRole;
import me.aco.marketplace_spring_ca.domain.intefrace.PasswordHasher;
import me.aco.marketplace_spring_ca.infrastructure.persistence.JpaUserRepository;

@Service
@Transactional
public class UpdateUserCommandHandler {

    private final JpaUserRepository userRepository;
    private final PasswordHasher passwordHasher;

    public UpdateUserCommandHandler(JpaUserRepository userRepository, PasswordHasher passwordHasher) {
        this.userRepository = userRepository;
        this.passwordHasher = passwordHasher;
    }

    public CompletableFuture<UserDto> handle(UpdateUserCommand command) {
        return CompletableFuture.supplyAsync(() -> {
            var user = userRepository.findById(command.id())
                    .orElseThrow(() -> new IllegalArgumentException("User not found"));

            user.setUsername(command.username());
            if (command.updatePassword()) {
                user.setPassword(passwordHasher.hash(command.password()));
            }
            user.setName(command.name());
            user.setEmail(command.email());
            user.setPhone(command.phone());
            user.setRole(UserRole.valueOf(command.role()));
            
            return new UserDto(userRepository.save(user));
        });
    }
    
}
