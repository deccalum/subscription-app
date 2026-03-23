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
class OperatorHttpMatrixTest {
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
                                + "\"lastName\":\"Tester\","
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
        // OPERATOR-02: Create operator (admin)
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/operators")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + adminToken)
                .content(
                        "{"
                                + "\"countryCode\":\"SE\","
                                + "\"name\":\"OperatorSuccess" + suffix + "\""
                                + "}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("OperatorSuccess" + suffix));
    }

    @Test
    void createValidationFailure() throws Exception {
        // OPERATOR-01: Create with missing name (bad DTO)
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/operators")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + adminToken)
                .content(
                        "{"
                                + "\"countryCode\":\"SE\","
                                + "\"name\":\"\""
                                + "}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateSuccessAndFailure() throws Exception {
        String operatorName = "OperatorUpdate" + suffix;

        // Create operator
        MvcResult createResult = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/operators")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + adminToken)
                .content(
                        "{"
                                + "\"countryCode\":\"SE\","
                                + "\"name\":\"" + operatorName + "\""
                                + "}"))
                .andExpect(status().isCreated())
                .andReturn();

        JsonNode createJson = objectMapper.readTree(createResult.getResponse().getContentAsString());
        Long operatorId = createJson.get("id").asLong();

        // OPERATOR-03: Update successfully
        mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/operators/" + operatorId)
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + adminToken)
                .content(
                        "{"
                                + "\"countryCode\":\"SE\","
                                + "\"name\":\"OperatorRenamed" + suffix + "\""
                                + "}"))
                .andExpect(status().isOk());

        // OPERATOR-04: Update with blank name (should fail)
        mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/operators/" + operatorId)
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + adminToken)
                .content(
                        "{"
                                + "\"countryCode\":\"SE\","
                                + "\"name\":\"\""
                                + "}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deleteSuccess() throws Exception {
        // Create operator
        MvcResult createResult = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/operators")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + adminToken)
                .content(
                        "{"
                                + "\"countryCode\":\"SE\","
                                + "\"name\":\"OperatorDelete" + suffix + "\""
                                + "}"))
                .andExpect(status().isCreated())
                .andReturn();

        JsonNode createJson = objectMapper.readTree(createResult.getResponse().getContentAsString());
        Long operatorId = createJson.get("id").asLong();

        // OPERATOR-05: Delete successfully
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/operators/" + operatorId).header("Authorization",
                "Bearer " + adminToken)).andExpect(status().isNoContent());
    }

    @Test
    void getNameIgnoreCaseSuccess() throws Exception {
        String operatorName = "OperatorIgnoreCase" + suffix;

        // Create operator
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/operators")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + adminToken)
                .content(
                        "{"
                                + "\"countryCode\":\"SE\","
                                + "\"name\":\"" + operatorName + "\""
                                + "}"))
                .andExpect(status().isCreated());

        // OP-REPO-01: Find by name ignoring case
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/operators/search/ignorecase")
                .param("name", operatorName.toLowerCase()).header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value(operatorName));
    }

    @Test
    void existsByNameSuccess() throws Exception {
        String operatorName = "OperatorExists" + suffix;

        // Create operator
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/operators")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + adminToken)
                .content(
                        "{"
                                + "\"countryCode\":\"SE\","
                                + "\"name\":\"" + operatorName + "\""
                                + "}"))
                .andExpect(status().isCreated());

        // OP-REPO-02: Exists by name
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/operators/exists").param("name", operatorName)
                .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(true));

        // OP-REPO-03: Exists by name (ignore case)
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/operators/exists")
                .param("name", operatorName.toLowerCase())
                .param("ignoreCase", "true")
                .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(true));
    }

    @Test
    void getAllSuccess() throws Exception {
        // Create a few operators
        for (int i = 0; i < 2; i++) {
            mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/operators")
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("Authorization", "Bearer " + adminToken)
                    .content(
                            "{"
                                    + "\"countryCode\":\"SE\","
                                    + "\"name\":\"OperatorList" + suffix + "_" + i + "\""
                                    + "}"))
                    .andExpect(status().isCreated());
        }

        // Get all operators
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/operators").header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }
}
