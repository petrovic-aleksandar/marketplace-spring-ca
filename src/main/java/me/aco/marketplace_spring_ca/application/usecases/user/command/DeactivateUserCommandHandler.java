package me.aco.marketplace_spring_ca.application.usecases.user.command;

import java.util.concurrent.CompletableFuture;


import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import me.aco.marketplace_spring_ca.application.dto.UserDto;
import me.aco.marketplace_spring_ca.application.exceptions.ResourceNotFoundException;
import me.aco.marketplace_spring_ca.infrastructure.persistence.JpaUserRepository;

@Service
@Transactional
public class DeactivateUserCommandHandler {

    private final JpaUserRepository userRepository;

    public DeactivateUserCommandHandler(JpaUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public CompletableFuture<UserDto> handle(DeactivateUserCommand command) {
        return CompletableFuture.supplyAsync(() -> {
            var user = userRepository.findById(command.id())
                    .orElseThrow(() -> new ResourceNotFoundException("User not found"));
            user.deactivate();
            return new UserDto(userRepository.save(user));
        });
    }
}
