package me.aco.marketplace_spring_ca.presentation.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.CompletableFuture;

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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.request;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.asyncDispatch;

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
            .thenReturn(CompletableFuture.completedFuture(user));
        
        // Act & Assert
        mockMvc.perform(get("/api/User/1"))
            .andExpect(request().asyncStarted())
            .andDo(result -> mockMvc.perform(asyncDispatch(result)))
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

        when(getAllUsersQueryHandler.handle(any())).thenReturn(CompletableFuture.completedFuture(users));
        
        // Act & Assert
        mockMvc.perform(get("/api/User"))
            .andExpect(request().asyncStarted())
            .andDo(result -> mockMvc.perform(asyncDispatch(result)))
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
            .thenReturn(CompletableFuture.completedFuture(user));
            
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
            .andExpect(request().asyncStarted())
            .andDo(result -> mockMvc.perform(asyncDispatch(result)))
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
            .thenReturn(CompletableFuture.completedFuture(user));

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
            .andExpect(request().asyncStarted())
            .andDo(result -> mockMvc.perform(asyncDispatch(result)))
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
            .thenReturn(CompletableFuture.completedFuture(user));

        // Act & Assert
        mockMvc.perform(post("/api/User/deactivate/1"))
            .andExpect(request().asyncStarted())
            .andDo(result -> mockMvc.perform(asyncDispatch(result)))
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

        when(activateUserCommandHandler.handle(any())).thenReturn(CompletableFuture.completedFuture(user));

        // Act & Assert
        mockMvc.perform(post("/api/User/activate/1"))
            .andExpect(request().asyncStarted())
            .andDo(result -> mockMvc.perform(asyncDispatch(result)))
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
