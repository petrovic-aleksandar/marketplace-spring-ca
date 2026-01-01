package me.aco.marketplace_spring_ca.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import me.aco.marketplace_spring_ca.application.dto.TokenDto;
import me.aco.marketplace_spring_ca.application.usecases.auth.command.LoginCommand;
import me.aco.marketplace_spring_ca.application.usecases.auth.command.RegisterCommand;
import me.aco.marketplace_spring_ca.application.usecases.user.command.UpdateUserCommand;
import me.aco.marketplace_spring_ca.domain.intefrace.PasswordHasher;
import me.aco.marketplace_spring_ca.infrastructure.persistence.JpaUserRepository;

@SpringBootTest
@AutoConfigureMockMvc
public class RegisterUpdateAndLogin {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private JpaUserRepository userRepository;
    @Autowired
    private PasswordHasher passwordHasher;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final String REGISTER_URI = "/api/Auth/register";
    private static final String LOGIN_URI = "/api/Auth/login";

    @Test
    public void testRegisterUpdateAndLogin() throws Exception {

        String suffix = UUID.randomUUID().toString();
        String username = "newuser_" + suffix;
        String email = "user_" + suffix + "@example.com";
        String updatedEmail = "updateduser_" + suffix + "@example.com";

        // Step 1: Register a new user
        RegisterCommand registerCommand = new RegisterCommand(
                username,
                "password123",
                email,
                "New User",
                "555-9999");

        // Perform registration
        mockMvc.perform(post(REGISTER_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerCommand)))
                .andExpect(status().isCreated());

        // Assert password is hashed in repository
        var registeredUserOpt = userRepository.findSingleByUsername(username);
        assertEquals(true, registeredUserOpt.isPresent(), "User should exist in repository after registration");
        assertTrue(passwordHasher.verify("password123", registeredUserOpt.get().getPassword()),
                "Password should match after registration");

        // Step 2: Login with a newly created user
        LoginCommand loginCommand = new LoginCommand(
                username,
                "password123");

        // Perform login
        var loginResponse = mockMvc.perform(post(LOGIN_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginCommand)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").isString())
                .andExpect(jsonPath("$.accessToken").isNotEmpty())
                .andExpect(jsonPath("$.refreshToken").isString())
                .andExpect(jsonPath("$.refreshToken").isNotEmpty())
                .andReturn();

        // Extract JWT token from login response
        TokenDto tokenDto = objectMapper.readValue(loginResponse.getResponse().getContentAsString(), TokenDto.class);
        String jwtToken = "Bearer " + tokenDto.accessToken();

        // Step 3: Verify user exists in repository
        var userOpt = userRepository.findSingleByUsername(username);
        assertEquals(true, userOpt.isPresent(), "User should exist in repository after registration");

        // Step 4: Update user
        UpdateUserCommand command = new UpdateUserCommand(
                userOpt.get().getId(),
                username,
                false,
                "",
                "Updated User",
                updatedEmail,
                "555-1111",
                "ADMIN");

        // Perform update
        mockMvc.perform(post("/api/User/" + userOpt.get().getId())
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", jwtToken)
                .content(objectMapper.writeValueAsString(command)))
                .andExpect(status().isOk());

        // Step 5: Change password
        UpdateUserCommand updCommand = new UpdateUserCommand(
                userOpt.get().getId(),
                username,
                true,
                "updatedPassword123",
                "Updated User",
                updatedEmail,
                "555-1111",
                "ADMIN");

        // Perform update
        mockMvc.perform(post("/api/User/" + userOpt.get().getId())
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", jwtToken)
                .content(objectMapper.writeValueAsString(updCommand)))
                .andExpect(status().isOk());

        // Assert password is changed in repository
        var updatedUserOpt = userRepository.findSingleByUsername(username);
        assertTrue(updatedUserOpt.isPresent(), "User should exist after password update");
        assertTrue(passwordHasher.verify("updatedPassword123", updatedUserOpt.get().getPassword()),
                "Password should match after update");

        // Step 6: Login with the updated password
        LoginCommand loginCommand2 = new LoginCommand(
                username,
                "updatedPassword123");

        // Perform login
        mockMvc.perform(post(LOGIN_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginCommand2)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").isString())
                .andExpect(jsonPath("$.accessToken").isNotEmpty())
                .andExpect(jsonPath("$.refreshToken").isString())
                .andExpect(jsonPath("$.refreshToken").isNotEmpty());
    }

}
