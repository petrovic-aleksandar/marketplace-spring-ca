package me.aco.marketplace_spring_ca.integration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.fasterxml.jackson.databind.ObjectMapper;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import me.aco.marketplace_spring_ca.application.usecases.auth.command.LoginCommand;
import me.aco.marketplace_spring_ca.application.usecases.auth.command.RegisterCommand;
import me.aco.marketplace_spring_ca.infrastructure.persistence.JpaUserRepository;


@SpringBootTest
@AutoConfigureMockMvc
public class RegisterAndLogin {


    @Autowired
    private JpaUserRepository userRepository;

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final String REGISTER_URI = "/api/Auth/register";
    private static final String LOGIN_URI = "/api/Auth/login";


    @BeforeEach
    public void setup() {
        // You can add setup logic here if needed
    }

    @Test
    public void testRegisterAndLogin() throws Exception {

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
        mockMvc.perform(MockMvcRequestBuilders.asyncDispatch(regResult))
            .andExpect(MockMvcResultMatchers.status().isCreated());
        
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
        mockMvc.perform(MockMvcRequestBuilders.asyncDispatch(loginResult))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.jsonPath("$.accessToken").isString())
            .andExpect(MockMvcResultMatchers.jsonPath("$.accessToken").isNotEmpty())
            .andExpect(MockMvcResultMatchers.jsonPath("$.refreshToken").isString())
            .andExpect(MockMvcResultMatchers.jsonPath("$.refreshToken").isNotEmpty());

        // 

    }
    
}
