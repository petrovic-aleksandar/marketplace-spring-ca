package me.aco.marketplace_spring_ca.application.usecases.user.query;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import me.aco.marketplace_spring_ca.application.dto.UserDto;
import me.aco.marketplace_spring_ca.infrastructure.persistence.JpaUserRepository;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GetAllUsersQueryHandler {

    private final JpaUserRepository userRepository;

    public List<UserDto> handle(GetAllUsersQuery query) {
        return userRepository.findAll()
                .stream()
                .map(UserDto::new)
                .toList();
    }

}
