package me.aco.marketplace_spring_ca.presentation.controllers;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import me.aco.marketplace_spring_ca.application.dto.UserDto;
import me.aco.marketplace_spring_ca.application.usecases.user.command.ActivateUserCommand;
import me.aco.marketplace_spring_ca.application.usecases.user.command.ActivateUserCommandHandler;
import me.aco.marketplace_spring_ca.application.usecases.user.command.AddUserCommand;
import me.aco.marketplace_spring_ca.application.usecases.user.command.AddUserCommandHandler;
import me.aco.marketplace_spring_ca.application.usecases.user.command.DeactivateUserCommand;
import me.aco.marketplace_spring_ca.application.usecases.user.command.DeactivateUserCommandHandler;
import me.aco.marketplace_spring_ca.application.usecases.user.command.UpdateUserCommand;
import me.aco.marketplace_spring_ca.application.usecases.user.command.UpdateUserCommandHandler;
import me.aco.marketplace_spring_ca.application.usecases.user.query.GetAllUsersQuery;
import me.aco.marketplace_spring_ca.application.usecases.user.query.GetAllUsersQueryHandler;
import me.aco.marketplace_spring_ca.application.usecases.user.query.GetUserByIdQuery;
import me.aco.marketplace_spring_ca.application.usecases.user.query.GetUserByIdQueryHandler;
import me.aco.marketplace_spring_ca.domain.enums.UserRole;

@RestController
@RequestMapping("/api/User")
@RequiredArgsConstructor
public class UsersController extends BaseController {

    private final GetUserByIdQueryHandler getUserByIdQueryHandler;
    private final GetAllUsersQueryHandler getAllUsersQueryHandler;
    private final AddUserCommandHandler addUserCommandHandler;
    private final UpdateUserCommandHandler updateUserCommandHandler;
    private final DeactivateUserCommandHandler deactivateUserCommandHandler;
    private final ActivateUserCommandHandler activateUserCommandHandler;

    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getById(@PathVariable Long id) {
        return ok(getUserByIdQueryHandler.handle(new GetUserByIdQuery(id)));
    }

    @GetMapping("/")
    public ResponseEntity<List<UserDto>> getAll() {
        return ok(getAllUsersQueryHandler.handle(new GetAllUsersQuery()));
    }

    @PostMapping
    public ResponseEntity<UserDto> create(@RequestBody AddUserCommand command) {
        return created(addUserCommandHandler.handle(command));
    }

    @PostMapping("/{id}")
    public ResponseEntity<UserDto> update(@PathVariable Long id, @RequestBody UpdateUserCommand command) {
        return ok(updateUserCommandHandler.handle(UpdateUserCommand.withId(id, command)));
    }

    @PostMapping("/deactivate/{id}")
    public ResponseEntity<UserDto> deactivate(@PathVariable Long id) {
        return ok(deactivateUserCommandHandler.handle(new DeactivateUserCommand(id)));
    }

    @PostMapping("/activate/{id}")
    public ResponseEntity<UserDto> activate(@PathVariable Long id) {
        return ok(activateUserCommandHandler.handle(new ActivateUserCommand(id)));
    }

    @GetMapping("/roles")
    public ResponseEntity<List<String>> getUserRoles() {
        List<String> roles = Arrays.stream(UserRole.values())
                .map(Enum::toString)
                .collect(Collectors.toList());
        return ResponseEntity.ok(roles);
    }
}
