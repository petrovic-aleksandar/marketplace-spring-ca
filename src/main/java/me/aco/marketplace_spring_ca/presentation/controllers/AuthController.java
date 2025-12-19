package me.aco.marketplace_spring_ca.presentation.controllers;

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
import me.aco.marketplace_spring_ca.application.usecases.AuthService;
import me.aco.marketplace_spring_ca.application.usecases.UserService;

@RestController
@RequestMapping("/api/auth")
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

    @PostMapping("/login")
    public ResponseEntity<TokenResp> login(@RequestBody LoginReq req) {
        User user = authService.authenticate(req);
        // Note: JWT token generation needs to be implemented
        TokenResp resp = new TokenResp(
                "access-token-" + user.getId(), // Placeholder
                authService.createAndSaveRefreshToken(user));
        return ResponseEntity.ok(resp);
    }

    @PostMapping("/register")
    public ResponseEntity<UserResp> register(@RequestBody UserRegReq req) {
        var existingUser = userRepository.findByEmail(req.getEmail());
        if (existingUser.isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
        
        User newUser = userService.toUser(req);
        User addedUser = userRepository.save(newUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(new UserResp(addedUser));
    }
}