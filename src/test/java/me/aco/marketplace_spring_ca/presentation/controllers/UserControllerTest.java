package me.aco.marketplace_spring_ca.presentation.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import me.aco.marketplace_spring_ca.application.dto.UserDto;
import me.aco.marketplace_spring_ca.application.usecases.user.command.ActivateUserCommandHandler;
import me.aco.marketplace_spring_ca.application.usecases.user.command.AddUserCommand;
import me.aco.marketplace_spring_ca.application.usecases.user.command.AddUserCommandHandler;
import me.aco.marketplace_spring_ca.application.usecases.user.command.DeactivateUserCommandHandler;
import me.aco.marketplace_spring_ca.application.usecases.user.command.UpdateUserCommand;
import me.aco.marketplace_spring_ca.application.usecases.user.command.UpdateUserCommandHandler;
import me.aco.marketplace_spring_ca.application.usecases.user.query.GetAllUsersQueryHandler;
import me.aco.marketplace_spring_ca.application.usecases.user.query.GetUserByIdQueryHandler;
import me.aco.marketplace_spring_ca.infrastructure.security.JwtTokenService;
import org.springframework.security.core.userdetails.UserDetailsService;

@WebMvcTest(UsersController.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private GetUserByIdQueryHandler getUserByIdQueryHandler;
    @MockitoBean
    private GetAllUsersQueryHandler getAllUsersQueryHandler;
    @MockitoBean
    private AddUserCommandHandler addUserCommandHandler;
    @MockitoBean
    private UpdateUserCommandHandler updateUserCommandHandler;
    @MockitoBean
    private DeactivateUserCommandHandler deactivateUserCommandHandler;
    @MockitoBean
    private ActivateUserCommandHandler activateUserCommandHandler;

    //these two are needed for security context
    @MockitoBean
    private UserDetailsService userDetailsService;
    @MockitoBean
    private JwtTokenService jwtTokenService;

    @Test
    void testGetUserById() throws Exception {

        // Arrange
        UserDto user = new UserDto(
            1L, 
            "user", 
            "User", 
            "user@example.com", 
            "555-1234", 
            BigDecimal.ZERO, 
            "USER", 
            true);

        when(getUserByIdQueryHandler.handle(any()))
            .thenReturn(user);
        
        // Act & Assert
        mockMvc.perform(get("/api/User/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void testGetAllUsers() throws Exception {

        // Arrange
        List<UserDto> users = List.of(
            new UserDto(
                1L, 
                "user", 
                "User", 
                "user@example.com", 
                "555-1234", 
                BigDecimal.ZERO, 
                "USER", 
                true
            ));

        when(getAllUsersQueryHandler.handle(any())).thenReturn(users);
        
        // Act & Assert
        mockMvc.perform(get("/api/User/"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    void testCreateUser() throws Exception {

        // Arrange
        UserDto user = new UserDto(
            2L,
            "newuser", 
            "New User", 
            "newuser@example.com", 
            "555-9999", 
            BigDecimal.ZERO, 
            "USER", 
            true);

        when(addUserCommandHandler.handle(any()))
            .thenReturn(user);
            
        String json = objectMapper.writeValueAsString(
            new AddUserCommand(
                "newuser", 
                "password", 
                "New User", 
                "newuser@example.com", 
                "555-9999", 
                "USER"
            ));

        // Act & Assert
        mockMvc.perform(post("/api/User")
                .contentType("application/json")
                .content(json))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value(2));
    }

    @Test
    void testUpdateUser() throws Exception {

        // Arrange
        UserDto user = new UserDto(
            1L, 
            "user", 
            "Updated User", 
            "updated@example.com", 
            "555-1111", 
            BigDecimal.ZERO, 
            "ADMIN", 
            true);

        when(updateUserCommandHandler.handle(any()))
            .thenReturn(user);

        String json = objectMapper.writeValueAsString(
            new UpdateUserCommand(
                1L, 
                "user", 
                false, 
                null, 
                "Updated User", 
                "updated@example.com", 
                "555-1111", 
                "ADMIN"
            ));

        // Act & Assert
        mockMvc.perform(post("/api/User/1")
                .contentType("application/json")
                .content(json))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void testDeactivateUser() throws Exception {

        // Arrange
        UserDto user = new UserDto(
            1L, 
            "user", 
            "User", 
            "user@example.com", 
            "555-1234", 
            BigDecimal.ZERO, 
            "USER", 
            false);

        when(deactivateUserCommandHandler.handle(any()))
            .thenReturn(user);

        // Act & Assert
        mockMvc.perform(post("/api/User/deactivate/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.active").value(false));
    }

    @Test
    void testActivateUser() throws Exception {

        // Arrange
        UserDto user = new UserDto(
            1L, 
            "user", 
            "User", 
            "user@example.com", 
            "555-1234", 
            BigDecimal.ZERO, 
            "USER", 
            true
        );

        when(activateUserCommandHandler.handle(any())).thenReturn(user);

        // Act & Assert
        mockMvc.perform(post("/api/User/activate/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.active").value(true));
    }

    @Test
    void testGetUserRoles() throws Exception {

        // Act & Assert
        mockMvc.perform(get("/api/User/roles"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0]").value("USER"))
            .andExpect(jsonPath("$[1]").value("ADMIN"));
    }

    
    
}
