package me.aco.marketplace_spring_ca.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import me.aco.marketplace_spring_ca.application.usecases.auth.command.LoginCommand;
import me.aco.marketplace_spring_ca.application.usecases.auth.command.RegisterCommand;
import me.aco.marketplace_spring_ca.application.usecases.user.command.UpdateUserCommand;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.asyncDispatch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@SpringBootTest
@AutoConfigureMockMvc
public class RegisterUpdateAndLogin {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final String REGISTER_URI = "/api/Auth/register";
    private static final String LOGIN_URI = "/api/Auth/login";

    @Test
    public void testRegisterUpdateAndLogin() throws Exception {

        // Step 1: Register a new user
        RegisterCommand registerCommand = new RegisterCommand(
                "newuser",
                "password123",
                "newuser@example.com",
                "New User",
                "555-9999"
        );

        // Perform registration
        var regResult = mockMvc.perform(post(REGISTER_URI)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(registerCommand)))
            .andReturn();

        // Async dispatch and assert 201 Created
        mockMvc.perform(asyncDispatch(regResult))
            .andDo(print())
            .andExpect(status().isCreated());
        
        // Step 2: Login with a newly created user
        LoginCommand loginCommand = new LoginCommand(
                "newuser",
                "password123"
        );

        // Perform login
        var loginResult = mockMvc.perform(post(LOGIN_URI)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(loginCommand)))
            .andReturn();

        // Async dispatch and assert 200 OK and token presence
        mockMvc.perform(asyncDispatch(loginResult))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.accessToken").isString())
            .andExpect(jsonPath("$.accessToken").isNotEmpty())
            .andExpect(jsonPath("$.refreshToken").isString())
            .andExpect(jsonPath("$.refreshToken").isNotEmpty());

        // Step 3: Update user
        UpdateUserCommand command = new UpdateUserCommand(
                1L,
                "newuser",
                false,
                "",
                "Updated User",
                "updateduser@example.com",
                "555-1111",
                "ADMIN"
        );

        // Perform update
        var updateResult = mockMvc.perform(post("/api/User/1")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(command)))
            .andReturn();

        // Async dispatch and assert 200 OK
        mockMvc.perform(asyncDispatch(updateResult))
            .andDo(print())
            .andExpect(status().isOk());

        // Assert changes
        assertEquals("newuser", updateResult.getResponse().getContentAsString().contains("Updated User"));

        // Step 4: Change password
        UpdateUserCommand updCommand = new UpdateUserCommand(
                1L,
                "newuser",
                true,
                "updatedPassword123",
                "Updated User",
                "updateduser@example.com",
                "555-1111",
                "ADMIN"
        );

        // Perform update
        var updateResult2 = mockMvc.perform(post("/api/User/1")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(updCommand)))
            .andReturn();

        // Async dispatch and assert 200 OK
        mockMvc.perform(asyncDispatch(updateResult2))
            .andDo(print())
            .andExpect(status().isOk());

        // Step 5: Login with the updated password
        LoginCommand loginCommand2 = new LoginCommand(
                "newuser",
                "updatedPassword123"
        );

        // Perform login
        var loginResult2 = mockMvc.perform(post(LOGIN_URI)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(loginCommand2)))
            .andReturn();

        // Async dispatch and assert 200 OK and token presence
        mockMvc.perform(asyncDispatch(loginResult2))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.accessToken").isString())
            .andExpect(jsonPath("$.accessToken").isNotEmpty())
            .andExpect(jsonPath("$.refreshToken").isString())
            .andExpect(jsonPath("$.refreshToken").isNotEmpty());
    }
    
}
