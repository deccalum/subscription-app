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
class UserHttpMatrixTest {
    @Autowired
    private WebApplicationContext context;

    @Autowired
    private FilterChainProxy springSecurityFilterChain;

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private String suffix;
    private String adminToken;

    @BeforeEach
    void setup() throws Exception {
        suffix = String.valueOf(System.nanoTime());
        mockMvc = MockMvcBuilders.webAppContextSetup(context).addFilters(springSecurityFilterChain).build();

        // Register and login admin for protected endpoints
        registerAndLoginAdmin();
    }

    private void registerAndLoginAdmin() throws Exception {
        String adminEmail = "admin_" + suffix + "@example.com";
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                        "{"
                                + "\"id\":null,"
                                + "\"credentials\":\"ROLE_ADMIN\","
                                + "\"email\":\"" + adminEmail + "\","
                                + "\"firstName\":\"Admin\","
                                + "\"lastName\":\"User\","
                                + "\"password\":\"Password123!\","
                                + "\"address\":\"Admin St\","
                                + "\"phoneNumber\":\"+46000000001\","
                                + "\"preferences\":\"admin\""
                                + "}"))
                .andExpect(status().isOk());

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"" + adminEmail + "\",\"password\":\"Password123!\"}"))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode json = objectMapper.readTree(result.getResponse().getContentAsString());
        adminToken = json.get("accessToken").asText();
    }

    @Test
    void createSuccess() throws Exception {
        String email = "user_" + suffix + "@example.com";

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/Users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                        "{"
                                + "\"id\":null,"
                                + "\"credentials\":\"ROLE_USER\","
                                + "\"email\":\"" + email + "\","
                                + "\"firstName\":\"NewUser\","
                                + "\"lastName\":\"Http\","
                                + "\"password\":\"Password123!\","
                                + "\"address\":\"User Street 10\","
                                + "\"phoneNumber\":\"+46000001010\","
                                + "\"preferences\":\"A\""
                                + "}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.email").value(email));
    }

    @Test
    void updateValidationFailure() throws Exception {
        String email = "user2_" + suffix + "@example.com";

        // Create user first
        MvcResult createResult = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/Users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                        "{"
                                + "\"id\":null,"
                                + "\"credentials\":\"ROLE_USER\","
                                + "\"email\":\"" + email + "\","
                                + "\"firstName\":\"TestUser\","
                                + "\"lastName\":\"Http\","
                                + "\"password\":\"Password123!\","
                                + "\"address\":\"User Street\","
                                + "\"phoneNumber\":\"+46000001010\","
                                + "\"preferences\":\"test\""
                                + "}"))
                .andExpect(status().isCreated())
                .andReturn();

        JsonNode createJson = objectMapper.readTree(createResult.getResponse().getContentAsString());
        Long userId = createJson.get("id").asLong();

        String userToken = loginUser(email, "Password123!");

        // Try to update with blank firstName - should fail
        mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/Users/" + userId)
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + userToken)
                .content(
                        "{"
                                + "\"id\":" + userId + ","
                                + "\"credentials\":\"ROLE_USER\","
                                + "\"email\":\"" + email + "\","
                                + "\"firstName\":\"\","
                                + "\"lastName\":\"Http\","
                                + "\"password\":\"Password123!\","
                                + "\"address\":\"User Street 11\","
                                + "\"phoneNumber\":\"+46000001011\","
                                + "\"preferences\":\"A-updated\""
                                + "}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void loginSuccess() throws Exception {
        String email = "user3_" + suffix + "@example.com";
        String password = "Password123!";

        // Create
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/Users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                        "{"
                                + "\"id\":null,"
                                + "\"credentials\":\"ROLE_USER\","
                                + "\"email\":\"" + email + "\","
                                + "\"firstName\":\"UserA\","
                                + "\"lastName\":\"Http\","
                                + "\"password\":\"" + password + "\","
                                + "\"address\":\"User Street 10\","
                                + "\"phoneNumber\":\"+46000001010\","
                                + "\"preferences\":\"A\""
                                + "}"))
                .andExpect(status().isCreated());

        // Login
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"" + email + "\",\"password\":\"" + password + "\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").exists());
    }

    @Test
    void updateSuccess() throws Exception {
        String email = "user4_" + suffix + "@example.com";

        // Create
        MvcResult createResult = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/Users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                        "{"
                                + "\"id\":null,"
                                + "\"credentials\":\"ROLE_USER\","
                                + "\"email\":\"" + email + "\","
                                + "\"firstName\":\"UserA\","
                                + "\"lastName\":\"Http\","
                                + "\"password\":\"Password123!\","
                                + "\"address\":\"User Street 10\","
                                + "\"phoneNumber\":\"+46000001010\","
                                + "\"preferences\":\"A\""
                                + "}"))
                .andExpect(status().isCreated())
                .andReturn();

        JsonNode createJson = objectMapper.readTree(createResult.getResponse().getContentAsString());
        Long userId = createJson.get("id").asLong();

        String userToken = loginUser(email, "Password123!");

        // Update successfully
        mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/Users/" + userId)
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + userToken)
                .content(
                        "{"
                                + "\"id\":" + userId + ","
                                + "\"credentials\":\"ROLE_USER\","
                                + "\"email\":\"" + email + "\","
                                + "\"firstName\":\"UserA-Updated\","
                                + "\"lastName\":\"Http\","
                                + "\"password\":\"Password123!\","
                                + "\"address\":\"User Street 11\","
                                + "\"phoneNumber\":\"+46000001011\","
                                + "\"preferences\":\"A-updated\""
                                + "}"))
                .andExpect(status().isOk());
    }

    @Test
    void deleteAndReadAfterDelete() throws Exception {
        String email = "user5_" + suffix + "@example.com";

        // Create
        MvcResult createResult = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/Users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                        "{"
                                + "\"id\":null,"
                                + "\"credentials\":\"ROLE_USER\","
                                + "\"email\":\"" + email + "\","
                                + "\"firstName\":\"UserA\","
                                + "\"lastName\":\"Http\","
                                + "\"password\":\"Password123!\","
                                + "\"address\":\"User Street 10\","
                                + "\"phoneNumber\":\"+46000001010\","
                                + "\"preferences\":\"A\""
                                + "}"))
                .andExpect(status().isCreated())
                .andReturn();

        JsonNode createJson = objectMapper.readTree(createResult.getResponse().getContentAsString());
        Long userId = createJson.get("id").asLong();

        String userToken = loginUser(email, "Password123!");

        // Delete
        mockMvc.perform(
                MockMvcRequestBuilders.delete("/api/v1/Users/" + userId).header("Authorization", "Bearer " + userToken))
                .andExpect(status().isNoContent());

        // Read after delete should be 404
        mockMvc.perform(
                MockMvcRequestBuilders.get("/api/v1/Users/" + userId).header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isNotFound());
    }

    @Test
    void existsByEmailSuccessAndFailure() throws Exception {
        String registeredEmail = "exist_" + suffix + "@example.com";

        // Create user
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/Users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                        "{"
                                + "\"id\":null,"
                                + "\"credentials\":\"ROLE_USER\","
                                + "\"email\":\"" + registeredEmail + "\","
                                + "\"firstName\":\"User\","
                                + "\"lastName\":\"Http\","
                                + "\"password\":\"Password123!\","
                                + "\"address\":\"Test St\","
                                + "\"phoneNumber\":\"+46000001010\","
                                + "\"preferences\":\"test\""
                                + "}"))
                .andExpect(status().isCreated());

        // USER-REPO-01: Check exists (true)
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/Users/exists").param("email", registeredEmail)
                .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(true));

        // USER-REPO-02: Check exists (false)
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/Users/exists")
                .param("email", "notexist_" + suffix + "@example.com").header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(false));
    }

    @Test
    void repositoryQueriesTemplate() throws Exception {
        // Placeholder for additional repository queries beyond exists
        // Current API coverage is sufficient for basic CRUD + exists
    }

    private String loginUser(String email, String password) throws Exception {
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"" + email + "\",\"password\":\"" + password + "\"}"))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode json = objectMapper.readTree(result.getResponse().getContentAsString());
        return json.get("accessToken").asText();
    }
}
