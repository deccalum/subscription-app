package se.lexicon.subscriptionapi.httpmatrix;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest
class AuthHttpMatrixTest {
    @Autowired
    private WebApplicationContext context;

    @Autowired
    private FilterChainProxy springSecurityFilterChain;

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private String suffix;

    @BeforeEach
    void setup() {
        suffix = String.valueOf(System.nanoTime());
        mockMvc = MockMvcBuilders.webAppContextSetup(context).addFilters(springSecurityFilterChain).build();
    }

    @Test
    void loginSuccess() throws Exception {
        String email = "testuser_" + suffix + "@example.com";
        String password = "Password123!";

        // Register first
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                        "{"
                                + "\"id\":null,"
                                + "\"credentials\":\"ROLE_USER\","
                                + "\"email\":\"" + email + "\","
                                + "\"firstName\":\"TestUser\","
                                + "\"lastName\":\"Http\","
                                + "\"password\":\"" + password + "\","
                                + "\"address\":\"Test St\","
                                + "\"phoneNumber\":\"+46123456789\","
                                + "\"preferences\":\"test\""
                                + "}"))
                .andExpect(status().isOk());

        // Login should succeed
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"" + email + "\",\"password\":\"" + password + "\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").exists());
    }

    @Test
    void loginFailures() throws Exception {
        String email = "testuser_" + suffix + "@example.com";
        String password = "Password123!";

        // Register
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                        "{"
                                + "\"id\":null,"
                                + "\"credentials\":\"ROLE_USER\","
                                + "\"email\":\"" + email + "\","
                                + "\"firstName\":\"TestUser\","
                                + "\"lastName\":\"Http\","
                                + "\"password\":\"" + password + "\","
                                + "\"address\":\"Test St\","
                                + "\"phoneNumber\":\"+46123456789\","
                                + "\"preferences\":\"test\""
                                + "}"))
                .andExpect(status().isOk());

        // AUTH-02: Invalid credentials
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/auth/login").contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"" + email + "\",\"password\":\"wrong\"}"))
                .andExpect(status().isForbidden());

        // AUTH-03: Invalid email format
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/auth/login").contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"not-an-email\",\"password\":\"password\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void registerSuccess() throws Exception {
        String email = "registertest_" + suffix + "@example.com";

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                        "{"
                                + "\"id\":null,"
                                + "\"credentials\":\"ROLE_USER\","
                                + "\"email\":\"" + email + "\","
                                + "\"firstName\":\"NewUser\","
                                + "\"lastName\":\"Tester\","
                                + "\"password\":\"Password123!\","
                                + "\"address\":\"Test Road\","
                                + "\"phoneNumber\":\"+46987654321\","
                                + "\"preferences\":\"register-test\""
                                + "}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(email));
    }

    @Test
    void registerFailures() throws Exception {
        String email = "unique_" + suffix + "@example.com";

        // Register first user
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                        "{"
                                + "\"id\":null,"
                                + "\"credentials\":\"ROLE_USER\","
                                + "\"email\":\"" + email + "\","
                                + "\"firstName\":\"User1\","
                                + "\"lastName\":\"Tester\","
                                + "\"password\":\"Password123!\","
                                + "\"address\":\"Test Road\","
                                + "\"phoneNumber\":\"+46987654321\","
                                + "\"preferences\":\"test\""
                                + "}"))
                .andExpect(status().isOk());

        // AUTH-05: Duplicate email
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                        "{"
                                + "\"id\":null,"
                                + "\"credentials\":\"ROLE_USER\","
                                + "\"email\":\"" + email + "\","
                                + "\"firstName\":\"User2\","
                                + "\"lastName\":\"Tester\","
                                + "\"password\":\"Password123!\","
                                + "\"address\":\"Test Road\","
                                + "\"phoneNumber\":\"+46987654321\","
                                + "\"preferences\":\"test\""
                                + "}"))
                .andExpect(status().isBadRequest());

        // AUTH-06: Invalid DTO (blank firstName + short password)
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                        "{"
                                + "\"id\":null,"
                                + "\"credentials\":\"ROLE_USER\","
                                + "\"email\":\"invalid_" + suffix + "@example.com\","
                                + "\"firstName\":\"\","
                                + "\"lastName\":\"User\","
                                + "\"password\":\"short\","
                                + "\"address\":\"Test Road\","
                                + "\"phoneNumber\":\"ABC\","
                                + "\"preferences\":\"invalid\""
                                + "}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void logoutSuccess() throws Exception {
        String email = "logoutuser_" + suffix + "@example.com";
        String password = "Password123!";

        // Register
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                        "{"
                                + "\"id\":null,"
                                + "\"credentials\":\"ROLE_USER\","
                                + "\"email\":\"" + email + "\","
                                + "\"firstName\":\"LogoutUser\","
                                + "\"lastName\":\"Tester\","
                                + "\"password\":\"" + password + "\","
                                + "\"address\":\"Test St\","
                                + "\"phoneNumber\":\"+46123456789\","
                                + "\"preferences\":\"logout-test\""
                                + "}"))
                .andExpect(status().isOk());

        // Login
        MvcResult loginResult = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"" + email + "\",\"password\":\"" + password + "\"}"))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode loginJson = objectMapper.readTree(loginResult.getResponse().getContentAsString());
        String token = loginJson.get("accessToken").asText();

        // Logout with valid token
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/auth/logout").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    void logoutMissingTokenFailure() throws Exception {
        // Logout without token should fail
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/auth/logout")).andExpect(status().isForbidden());
    }
}
