package me.aco.marketplace_spring_ca.application.usecases.user.query;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import me.aco.marketplace_spring_ca.application.dto.UserDto;
import me.aco.marketplace_spring_ca.application.exceptions.ResourceNotFoundException;
import me.aco.marketplace_spring_ca.infrastructure.persistence.JpaUserRepository;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GetUserByIdQueryHandler {

    private final JpaUserRepository userRepository;

    public UserDto handle(GetUserByIdQuery query) {

        validateQuery(query);

        return userRepository.findById(query.id())
                .map(UserDto::new)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    private void validateQuery(GetUserByIdQuery query) {
        if (query.id() == null)
            throw new IllegalArgumentException("User ID cannot be null");
    }

}
