package me.aco.marketplace_spring_ca.presentation.controllers;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import me.aco.marketplace_spring_ca.application.dto.UserReq;
import me.aco.marketplace_spring_ca.application.dto.UserDto;
import me.aco.marketplace_spring_ca.application.exceptions.ResourceNotFoundException;
import me.aco.marketplace_spring_ca.domain.enums.UserRole;
import me.aco.marketplace_spring_ca.infrastructure.persistence.JpaUserRepository;
import me.aco.marketplace_spring_ca.application.usecases.UserService;

@RestController
@RequestMapping("/api/users")
public class UsersController {

    private final JpaUserRepository userRepository;
    private final UserService userService;

    public UsersController(JpaUserRepository userRepository, UserService userService) {
        this.userRepository = userRepository;
        this.userService = userService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUserById(@PathVariable Long id) {
        var user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return ResponseEntity.ok(new UserDto(user));
    }

    @GetMapping
    public ResponseEntity<List<UserDto>> getAllUsers() {
        var users = userRepository.findAll();
        var resp = users.stream().map(UserDto::new).collect(Collectors.toList());
        return ResponseEntity.ok(resp);
    }

    @PostMapping
    public ResponseEntity<UserDto> createUser(@RequestBody UserReq req) {
        var existingUser = userRepository.findSingleByUsername(req.username());
        if (existingUser.isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }

        var newUser = userService.toUser(req);
        var addedUser = userRepository.save(newUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(new UserDto(addedUser));
    }

    @PostMapping("/{id}")
    public ResponseEntity<UserDto> updateUser(@PathVariable Long id, @RequestBody UserReq req) {
        var user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        var updatedUser = userService.update(req, user);
        return ResponseEntity.ok(new UserDto(updatedUser));
    }

    @PostMapping("/deactivate/{id}")
    public ResponseEntity<UserDto> deactivateUser(@PathVariable Long id) {
        var user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        user.deactivate();
        var updatedUser = userRepository.save(user);
        return ResponseEntity.ok(new UserDto(updatedUser));
    }

    @PostMapping("/activate/{id}")
    public ResponseEntity<UserDto> activateUser(@PathVariable Long id) {
        var user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        user.activate();
        var updatedUser = userRepository.save(user);
        return ResponseEntity.ok(new UserDto(updatedUser));
    }

    @GetMapping("/roles")
    public ResponseEntity<List<String>> getUserRoles() {
        List<String> roles = Arrays.stream(UserRole.values())
                .map(Enum::toString)
                .collect(Collectors.toList());
        return ResponseEntity.ok(roles);
    }
}
