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
class PlanHttpMatrixTest {
    @Autowired
    private WebApplicationContext context;

    @Autowired
    private FilterChainProxy springSecurityFilterChain;

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private String suffix;
    private String adminToken;
    private Long operatorId;

    @BeforeEach
    void setup() throws Exception {
        suffix = String.valueOf(System.nanoTime());
        mockMvc = MockMvcBuilders.webAppContextSetup(context).addFilters(springSecurityFilterChain).build();

        registerAndLoginAdmin();
        createOperatorAndPlan();
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

    private void createOperatorAndPlan() throws Exception {
        // Create operator
        MvcResult operatorResult = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/operators")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + adminToken)
                .content(
                        "{"
                                + "\"countryCode\":\"SE\","
                                + "\"name\":\"PlanTestOperator" + suffix + "\""
                                + "}"))
                .andExpect(status().isCreated())
                .andReturn();

        JsonNode operatorJson = objectMapper.readTree(operatorResult.getResponse().getContentAsString());
        operatorId = operatorJson.get("id").asLong();

        // Create plan (seed for queries)
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/plans")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + adminToken)
                .content(
                        "{"
                                + "\"kind\":\"INTERNET\","
                                + "\"name\":\"PlanSeed" + suffix + "\","
                                + "\"operator\":" + operatorId + ","
                                + "\"price\":199.00,"
                                + "\"status\":\"ACTIVE\","
                                + "\"uploadSpeedMbps\":100,"
                                + "\"downloadSpeedMbps\":500"
                                + "}"))
                .andExpect(status().isCreated());
    }

    @Test
    void createSuccess() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/plans")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + adminToken)
                .content(
                        "{"
                                + "\"kind\":\"INTERNET\","
                                + "\"name\":\"NewPlan" + suffix + "\","
                                + "\"operator\":" + operatorId + ","
                                + "\"price\":299.99,"
                                + "\"status\":\"ACTIVE\","
                                + "\"uploadSpeedMbps\":150,"
                                + "\"downloadSpeedMbps\":600"
                                + "}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("NewPlan" + suffix));
    }

    @Test
    void findByNameSuccessAndFailure() throws Exception {
        // PLAN-REPO-01: Find by name (success)
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/plans/name/PlanSeed" + suffix).header("Authorization",
                "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists());

        // PLAN-REPO-02: Find by name (not found)
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/plans/name/NoSuchPlan" + suffix).header("Authorization",
                "Bearer " + adminToken)).andExpect(status().isNotFound());
    }

    @Test
    void repositoryFiltersSuccess() throws Exception {
        // PLAN-REPO-03: Find by operator id
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/plans/operator/" + operatorId).header("Authorization",
                "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());

        // PLAN-REPO-04: Find by price range
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/plans/price").param("min", "100").param("max", "300")
                .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());

        // PLAN-REPO-05: Find by status
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/plans/status").param("status", "ACTIVE")
                .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void kindQueriesSuccess() throws Exception {
        // PLAN-REPO-06: List internet plans
        mockMvc.perform(
                MockMvcRequestBuilders.get("/api/v1/plans/internet").header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());

        // PLAN-REPO-07: List cellular plans
        mockMvc.perform(
                MockMvcRequestBuilders.get("/api/v1/plans/cellular").header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void operatorExistenceAndCountSuccess() throws Exception {
        // PLAN-REPO-08: Exists by operator id
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/plans/exists/operator/" + operatorId)
                .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(true));

        // PLAN-REPO-09: Count by operator id and status
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/plans/count/operator/" + operatorId + "/status")
                .param("status", "ACTIVE")
                .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isNumber());
    }
}
