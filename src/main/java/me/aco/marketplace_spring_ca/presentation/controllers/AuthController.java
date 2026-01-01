package me.aco.marketplace_spring_ca.presentation.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import me.aco.marketplace_spring_ca.application.dto.TokenDto;
import me.aco.marketplace_spring_ca.application.dto.UserDto;
import me.aco.marketplace_spring_ca.application.usecases.auth.command.LoginCommand;
import me.aco.marketplace_spring_ca.application.usecases.auth.command.LoginCommandHandler;
import me.aco.marketplace_spring_ca.application.usecases.auth.command.RefreshTokenCommand;
import me.aco.marketplace_spring_ca.application.usecases.auth.command.RefreshTokenCommandHandler;
import me.aco.marketplace_spring_ca.application.usecases.auth.command.RegisterCommand;
import me.aco.marketplace_spring_ca.application.usecases.auth.command.RegisterCommandHandler;
import me.aco.marketplace_spring_ca.application.usecases.auth.command.RevokeTokenCommand;
import me.aco.marketplace_spring_ca.application.usecases.auth.command.RevokeTokenCommandHandler;
import me.aco.marketplace_spring_ca.application.usecases.auth.command.UpdateSelfCommand;
import me.aco.marketplace_spring_ca.application.usecases.auth.command.UpdateSelfCommandHandler;
import me.aco.marketplace_spring_ca.application.usecases.user.query.GetUserByIdQuery;
import me.aco.marketplace_spring_ca.application.usecases.user.query.GetUserByIdQueryHandler;
import me.aco.marketplace_spring_ca.infrastructure.security.SelfOwner;

@RestController
@RequestMapping("/api/Auth")
@RequiredArgsConstructor
public class AuthController extends BaseController {

    private final LoginCommandHandler loginCommandHandler;
    private final RegisterCommandHandler registerCommandHandler;
    private final RefreshTokenCommandHandler refreshTokenCommandHandler;
    private final RevokeTokenCommandHandler revokeTokenCommandHandler;
    private final UpdateSelfCommandHandler updateSelfCommandHandler;
    private final GetUserByIdQueryHandler getUserByIdQueryHandler;

    @GetMapping("/ping")
    public String ping() {
        return "pong";
    }

    @PostMapping(value = "/login")
    public ResponseEntity<TokenDto> login(@RequestBody LoginCommand command) {
        return ok(loginCommandHandler.handle(command));
    }

    @PostMapping(value = "/register")
    public ResponseEntity<UserDto> register(@RequestBody RegisterCommand command) {
        return created(registerCommandHandler.handle(command));
    }

    @PostMapping(value = "/refresh-token")
    public ResponseEntity<TokenDto> refreshToken(@RequestBody RefreshTokenCommand command) {
        return ok(refreshTokenCommandHandler.handle(command));
    }

    @PostMapping(value = "/revoke-token")
    public ResponseEntity<Long> revokeToken(@RequestBody RevokeTokenCommand command) {
        return ok(revokeTokenCommandHandler.handle(command));
    }

    @GetMapping(value = "/self-info/{id}")
    @SelfOwner
    public ResponseEntity<UserDto> getSelf(@PathVariable Long id) {
        return ok(getUserByIdQueryHandler.handle(new GetUserByIdQuery(id)));
    }

    @PostMapping(value = "/update-self/{id}")
    @SelfOwner
    public ResponseEntity<UserDto> updateSelf(@PathVariable Long id, @RequestBody UpdateSelfCommand command) {
        return ok(updateSelfCommandHandler.handle(UpdateSelfCommand.withId(id, command)));
    }

}