package me.aco.marketplace_spring_ca.application.usecases.user.query;

import java.util.concurrent.CompletableFuture;

import org.springframework.stereotype.Service;

import me.aco.marketplace_spring_ca.application.dto.UserDto;
import me.aco.marketplace_spring_ca.application.exceptions.ResourceNotFoundException;
import me.aco.marketplace_spring_ca.infrastructure.persistence.JpaUserRepository;

@Service
public class GetUserByIdQueryHandler {

    private final JpaUserRepository userRepository;

    public GetUserByIdQueryHandler(JpaUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public CompletableFuture<UserDto> handle(GetUserByIdQuery query) {
        return CompletableFuture.supplyAsync(() -> userRepository.findById(query.id())
                .map(UserDto::new)
                .orElseThrow(() -> new ResourceNotFoundException("User not found")));
    }
    
}
