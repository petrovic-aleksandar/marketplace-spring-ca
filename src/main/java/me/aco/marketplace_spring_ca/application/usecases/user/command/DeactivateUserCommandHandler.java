package me.aco.marketplace_spring_ca.application.usecases.user.command;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import me.aco.marketplace_spring_ca.application.dto.UserDto;
import me.aco.marketplace_spring_ca.application.exceptions.ResourceNotFoundException;
import me.aco.marketplace_spring_ca.domain.entities.User;
import me.aco.marketplace_spring_ca.infrastructure.persistence.JpaUserRepository;

@Service
@Transactional
@RequiredArgsConstructor
public class DeactivateUserCommandHandler {

    private final JpaUserRepository userRepository;

    public UserDto handle(DeactivateUserCommand command) {

        validateCommand(command);

        var user = fetchUser(command.id());

        user.deactivate();
        user = save(user);
        return new UserDto(user);
    }

    private void validateCommand(DeactivateUserCommand command) {
        if (command.id() == null)
            throw new IllegalArgumentException("User ID cannot be null");
    }

    private User fetchUser(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    private User save(User user) {
        return userRepository.save(user);
    }
}
