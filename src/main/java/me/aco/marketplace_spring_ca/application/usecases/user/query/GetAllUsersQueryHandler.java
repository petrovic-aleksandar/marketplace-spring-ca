package me.aco.marketplace_spring_ca.application.usecases.user.query;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import me.aco.marketplace_spring_ca.application.dto.UserDto;
import me.aco.marketplace_spring_ca.infrastructure.persistence.JpaUserRepository;

public class GetAllUsersQueryHandler {

    private final JpaUserRepository userRepository;

    public GetAllUsersQueryHandler(JpaUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public CompletableFuture<List<UserDto>> handle(GetAllUsersQuery query) {
        return CompletableFuture.supplyAsync(() -> userRepository.findAll()
                .stream()
                .map(UserDto::new)
                .toList());
    }
    
}
