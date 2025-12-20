package me.aco.marketplace_spring_ca.presentation.controllers;

import java.util.concurrent.CompletableFuture;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import me.aco.marketplace_spring_ca.application.dto.TokenDto;
import me.aco.marketplace_spring_ca.application.dto.UserDto;
import me.aco.marketplace_spring_ca.application.usecases.auth.command.LoginCommand;
import me.aco.marketplace_spring_ca.application.usecases.auth.command.LoginCommandHandler;
import me.aco.marketplace_spring_ca.application.usecases.auth.command.RegisterCommand;
import me.aco.marketplace_spring_ca.application.usecases.auth.command.RegisterCommandHandler;
import me.aco.marketplace_spring_ca.application.usecases.auth.command.RefreshTokenCommand;
import me.aco.marketplace_spring_ca.application.usecases.auth.command.RefreshTokenCommandHandler;
import me.aco.marketplace_spring_ca.application.usecases.auth.command.RevokeTokenCommand;
import me.aco.marketplace_spring_ca.application.usecases.auth.command.RevokeTokenCommandHandler;

@RestController
@RequestMapping("/api/Auth")
public class AuthController {

    private final LoginCommandHandler loginCommandHandler;
    private final RegisterCommandHandler registerCommandHandler;
    private final RefreshTokenCommandHandler refreshTokenCommandHandler;
    private final RevokeTokenCommandHandler revokeTokenCommandHandler;

    public AuthController(LoginCommandHandler loginCommandHandler, RegisterCommandHandler registerCommandHandler,
            RefreshTokenCommandHandler refreshTokenCommandHandler, RevokeTokenCommandHandler revokeTokenCommandHandler) {
        this.loginCommandHandler = loginCommandHandler;
        this.registerCommandHandler = registerCommandHandler;
        this.refreshTokenCommandHandler = refreshTokenCommandHandler;
        this.revokeTokenCommandHandler = revokeTokenCommandHandler;
    }

    @GetMapping("/ping")
    public String ping() {
        return "pong";
    }

    @PostMapping(value = "/login")
    public CompletableFuture<ResponseEntity<TokenDto>> login(@RequestBody LoginCommand command) {
        return loginCommandHandler.handle(command)
                .thenApply(tokenDto -> ResponseEntity.ok(tokenDto))
                .exceptionally(ex -> ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
    }

    @PostMapping(value = "/register")
    public CompletableFuture<ResponseEntity<UserDto>> register(@RequestBody RegisterCommand command) {
        return registerCommandHandler.handle(command)
                .thenApply(userResp -> ResponseEntity.ok(userResp))
                .exceptionally(ex -> ResponseEntity.status(HttpStatus.BAD_REQUEST).build());
    }

    @PostMapping(value = "/refresh-token")
    public CompletableFuture<ResponseEntity<Long>> refreshToken(@RequestBody RefreshTokenCommand command) {
        return refreshTokenCommandHandler.handle(command)
                .thenApply(userId -> ResponseEntity.ok(userId))
                .exceptionally(ex -> ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
    }

    @PostMapping(value = "/revoke-token")
    public CompletableFuture<ResponseEntity<Long>> revokeToken(@RequestBody RevokeTokenCommand command) {
        return revokeTokenCommandHandler.handle(command)
                .thenApply(userId -> ResponseEntity.ok(userId))
                .exceptionally(ex -> ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
    }
}