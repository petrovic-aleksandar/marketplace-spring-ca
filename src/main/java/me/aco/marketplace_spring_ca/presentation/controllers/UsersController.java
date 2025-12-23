package me.aco.marketplace_spring_ca.presentation.controllers;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import me.aco.marketplace_spring_ca.application.dto.UserDto;
import me.aco.marketplace_spring_ca.domain.enums.UserRole;
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

@RestController
@RequestMapping("/api/users")
public class UsersController extends BaseController {

    private final GetUserByIdQueryHandler getUserByIdQueryHandler;
    private final GetAllUsersQueryHandler getAllUsersQueryHandler;
    private final AddUserCommandHandler addUserCommandHandler;
    private final UpdateUserCommandHandler updateUserCommandHandler;
    private final DeactivateUserCommandHandler deactivateUserCommandHandler;
    private final ActivateUserCommandHandler activateUserCommandHandler;

    public UsersController(GetUserByIdQueryHandler getUserByIdQueryHandler,
            GetAllUsersQueryHandler getAllUsersQueryHandler,
            AddUserCommandHandler addUserCommandHandler,
            UpdateUserCommandHandler updateUserCommandHandler,
            DeactivateUserCommandHandler deactivateUserCommandHandler,
            ActivateUserCommandHandler activateUserCommandHandler) {
        this.getUserByIdQueryHandler = getUserByIdQueryHandler;
        this.getAllUsersQueryHandler = getAllUsersQueryHandler;
        this.addUserCommandHandler = addUserCommandHandler;
        this.updateUserCommandHandler = updateUserCommandHandler;
        this.deactivateUserCommandHandler = deactivateUserCommandHandler;
        this.activateUserCommandHandler = activateUserCommandHandler;
    }

    @GetMapping("/{id}")
    public CompletableFuture<ResponseEntity<UserDto>> getUserById(@PathVariable GetUserByIdQuery query) {
        return getUserByIdQueryHandler.handle(query)
                .thenApply(ResponseEntity::ok);
        
    }

    @GetMapping
    public CompletableFuture<ResponseEntity<List<UserDto>>> getAllUsers(@PathVariable GetAllUsersQuery query) {
        return getAllUsersQueryHandler.handle(query)
                .thenApply(ResponseEntity::ok);
    }

    @PostMapping
    public CompletableFuture<ResponseEntity<UserDto>> createUser(@RequestBody AddUserCommand command) {
        return addUserCommandHandler.handle(command)
                .thenApply(this::created);
        
    }

    @PostMapping("/{id}")
    public CompletableFuture<ResponseEntity<UserDto>> updateUser(@PathVariable Long id, @RequestBody UpdateUserCommand command) {
        return updateUserCommandHandler.handle(UpdateUserCommand.withId(id, command))
                .thenApply(ResponseEntity::ok);
    }

    @PostMapping("/deactivate/{id}")
    public CompletableFuture<ResponseEntity<UserDto>> deactivateUser(@PathVariable Long id) {
        return deactivateUserCommandHandler.handle(new DeactivateUserCommand(id))
                .thenApply(ResponseEntity::ok);
    }

    @PostMapping("/activate/{id}")
    public CompletableFuture<ResponseEntity<UserDto>> activateUser(@PathVariable Long id) {
        return activateUserCommandHandler.handle(new ActivateUserCommand(id))
                .thenApply(ResponseEntity::ok);
    }

    @GetMapping("/roles")
    public ResponseEntity<List<String>> getUserRoles() {
        List<String> roles = Arrays.stream(UserRole.values())
                .map(Enum::toString)
                .collect(Collectors.toList());
        return ResponseEntity.ok(roles);
    }
}
