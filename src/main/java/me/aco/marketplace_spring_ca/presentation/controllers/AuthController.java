package me.aco.marketplace_spring_ca.presentation.controllers;

import java.util.concurrent.CompletableFuture;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import me.aco.marketplace_spring_ca.application.dto.LoginReq;
import me.aco.marketplace_spring_ca.application.dto.TokenResp;
import me.aco.marketplace_spring_ca.application.dto.UserRegReq;
import me.aco.marketplace_spring_ca.application.dto.UserResp;
import me.aco.marketplace_spring_ca.domain.entities.User;
import me.aco.marketplace_spring_ca.infrastructure.persistence.JpaUserRepository;
import me.aco.marketplace_spring_ca.infrastructure.security.JWTUtil;
import me.aco.marketplace_spring_ca.application.usecases.AuthService;
import me.aco.marketplace_spring_ca.application.usecases.UserService;

@RestController
@RequestMapping("/api/Auth")
public class AuthController {

    private final AuthService authService;
    private final JpaUserRepository userRepository;
    private final UserService userService;

    public AuthController(AuthService authService, JpaUserRepository userRepository, UserService userService) {
        this.authService = authService;
        this.userRepository = userRepository;
        this.userService = userService;
    }

    @GetMapping("/ping")
    public String ping() {
        return "pong";
    }

    @PostMapping(value = "/login")
    public CompletableFuture<ResponseEntity<TokenResp>> login(@RequestBody LoginReq req) {
        return CompletableFuture.supplyAsync(() -> {
            User user = authService.authenticate(req);
            TokenResp resp = new TokenResp(
                    JWTUtil.createToken(user),
                    authService.createAndSaveRefreshToken(user));
            return ResponseEntity.ok(resp);
        });
    }

    @PostMapping(value = "/register")
    public CompletableFuture<ResponseEntity<UserResp>> register(@RequestBody UserRegReq req) {

        return CompletableFuture.supplyAsync(() -> userRepository.findSingleByUsername(req.getUsername())
                .map(existingUser -> ResponseEntity.badRequest().<UserResp>build())
                .orElseGet(() -> {
                    User newUser = userService.toUser(req);
                    if (newUser == null)
                        return ResponseEntity.internalServerError().<UserResp>build();
                    User addedUser = userRepository.save(newUser);
                    return ResponseEntity.ok(new UserResp(addedUser));
                }));
    }
}