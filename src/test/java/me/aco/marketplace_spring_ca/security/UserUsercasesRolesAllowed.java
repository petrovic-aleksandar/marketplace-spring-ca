package me.aco.marketplace_spring_ca.security;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import me.aco.marketplace_spring_ca.application.dto.TokenDto;
import me.aco.marketplace_spring_ca.application.dto.UserDto;
import me.aco.marketplace_spring_ca.application.usecases.auth.command.LoginCommand;
import me.aco.marketplace_spring_ca.application.usecases.auth.command.RegisterCommand;
import me.aco.marketplace_spring_ca.application.usecases.user.command.AddUserCommand;
import me.aco.marketplace_spring_ca.domain.enums.UserRole;
import me.aco.marketplace_spring_ca.infrastructure.persistence.JpaUserRepository;

@SpringBootTest
@AutoConfigureMockMvc
public class UserUsercasesRolesAllowed {

    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private JpaUserRepository userRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private String adminToken;

    @BeforeEach
    void setUp() throws Exception {
        // Create an admin user for testing
        String adminSuffix = UUID.randomUUID().toString();
        String adminUsername = "admin_" + adminSuffix;
        String adminEmail = "admin_" + adminSuffix + "@example.com";

        // Register admin user
        RegisterCommand registerCommand = new RegisterCommand(
                adminUsername,
                "admin123",
                adminEmail,
                "Admin User",
                "555-0000");

        mockMvc.perform(post("/api/Auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerCommand)))
                .andExpect(status().isCreated());

        // Get the registered admin user and update their role to ADMIN directly
        var adminUser = userRepository.findSingleByUsername(adminUsername).orElseThrow();
        adminUser.setRole(UserRole.ADMIN);
        userRepository.save(adminUser);

        // Login to get admin token
        LoginCommand loginCommand = new LoginCommand(adminUsername, "admin123");
        String tokenResponse = mockMvc.perform(post("/api/Auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginCommand)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        TokenDto tokenDto = objectMapper.readValue(tokenResponse, TokenDto.class);
        adminToken = tokenDto.accessToken();
    }

    @Test
    void testAdminAllowed() throws Exception {
        // Step 1: Create a new admin user
        String suffix = UUID.randomUUID().toString();
        String username = "newadmin_" + suffix;
        String email = "newadmin_" + suffix + "@example.com";

        AddUserCommand addUserCommand = new AddUserCommand(
                username,
                "password123",
                email,
                "New Admin User",
                "555-9999",
                "ADMIN");

        String createResponse = mockMvc.perform(post("/api/User")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(addUserCommand))
                .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        UserDto createdUser = objectMapper.readValue(createResponse, UserDto.class);
        assert createdUser.id() != null : "Created user ID should not be null";
            
        // Step 2: Admin fetches all users (should succeed)
        mockMvc.perform(get("/api/User/")
                .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk());
    }

    @Test
    void testUserForbidden() throws Exception {
        // Step 1: Create a regular user via registration
        String suffix = UUID.randomUUID().toString();
        String userUsername = "regularuser_" + suffix;
        String userEmail = "regularuser_" + suffix + "@example.com";

        RegisterCommand registerCommand = new RegisterCommand(
                userUsername,
                "user123",
                userEmail,
                "Regular User",
                "555-1111");

        mockMvc.perform(post("/api/Auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerCommand)))
                .andExpect(status().isCreated());

        // Step 2: Login as regular user to get their token
        LoginCommand loginCommand = new LoginCommand(userUsername, "user123");
        String tokenResponse = mockMvc.perform(post("/api/Auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginCommand)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        TokenDto tokenDto = objectMapper.readValue(tokenResponse, TokenDto.class);
        String userToken = tokenDto.accessToken();

        // Step 3: Regular user tries to fetch all users (should fail with 403 Forbidden)
        mockMvc.perform(get("/api/User/")
                .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isForbidden());
    }
    
}
