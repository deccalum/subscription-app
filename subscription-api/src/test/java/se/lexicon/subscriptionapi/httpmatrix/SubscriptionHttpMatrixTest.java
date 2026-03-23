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
class SubscriptionHttpMatrixTest {
    @Autowired
    private WebApplicationContext context;

    @Autowired
    private FilterChainProxy springSecurityFilterChain;

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private String suffix;
    private String adminToken;
    private String userToken;
    private Long operatorId;
    private Long planId;
    private Long userId;

    @BeforeEach
    void setup() throws Exception {
        suffix = String.valueOf(System.nanoTime());
        mockMvc = MockMvcBuilders.webAppContextSetup(context).addFilters(springSecurityFilterChain).build();

        registerAndLoginAdmin();
        registerAndLoginUser();
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

    private void registerAndLoginUser() throws Exception {
        String userEmail = "user_" + suffix + "@example.com";
        MvcResult registerResult = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                        "{"
                                + "\"id\":null,"
                                + "\"credentials\":\"ROLE_USER\","
                                + "\"email\":\"" + userEmail + "\","
                                + "\"firstName\":\"User\","
                                + "\"lastName\":\"Tester\","
                                + "\"password\":\"Password123!\","
                                + "\"address\":\"User St\","
                                + "\"phoneNumber\":\"+46123456789\","
                                + "\"preferences\":\"user\""
                                + "}"))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode registerJson = objectMapper.readTree(registerResult.getResponse().getContentAsString());
        userId = registerJson.get("id").asLong();

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"" + userEmail + "\",\"password\":\"Password123!\"}"))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode json = objectMapper.readTree(result.getResponse().getContentAsString());
        userToken = json.get("accessToken").asText();
    }

    private void createOperatorAndPlan() throws Exception {
        // Create operator
        MvcResult operatorResult = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/operators")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + adminToken)
                .content(
                        "{"
                                + "\"countryCode\":\"SE\","
                                + "\"name\":\"SubsTestOperator" + suffix + "\""
                                + "}"))
                .andExpect(status().isCreated())
                .andReturn();

        JsonNode operatorJson = objectMapper.readTree(operatorResult.getResponse().getContentAsString());
        operatorId = operatorJson.get("id").asLong();

        // Create plan
        MvcResult planResult = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/plans")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + adminToken)
                .content(
                        "{"
                                + "\"kind\":\"INTERNET\","
                                + "\"name\":\"SubsPlan" + suffix + "\","
                                + "\"operator\":" + operatorId + ","
                                + "\"price\":199.00,"
                                + "\"status\":\"ACTIVE\","
                                + "\"uploadSpeedMbps\":100,"
                                + "\"downloadSpeedMbps\":500"
                                + "}"))
                .andExpect(status().isCreated())
                .andReturn();

        JsonNode planJson = objectMapper.readTree(planResult.getResponse().getContentAsString());
        planId = planJson.get("id").asLong();
    }

    @Test
    void createSuccess() throws Exception {
        // SUBS-02: Create subscription successfully
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/subscriptions")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + adminToken)
                .content(
                        "{"
                                + "\"operatorId\":" + operatorId + ","
                                + "\"planId\":" + planId + ","
                                + "\"userId\":" + userId + ","
                                + "\"status\":\"ACTIVE\""
                                + "}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.status").value("ACTIVE"));
    }

    @Test
    void createFailures() throws Exception {
        // SUBS-01: Try to create with non-existent IDs - should fail
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/subscriptions")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + adminToken)
                .content(
                        "{"
                                + "\"operatorId\":99999999,"
                                + "\"planId\":99999999,"
                                + "\"userId\":99999999,"
                                + "\"status\":\"ACTIVE\""
                                + "}"))
                .andExpect(status().isConflict());
    }

    @Test
    void readAndStatusSuccess() throws Exception {
        // Create subscription first
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/subscriptions")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + adminToken)
                .content(
                        "{"
                                + "\"operatorId\":" + operatorId + ","
                                + "\"planId\":" + planId + ","
                                + "\"userId\":" + userId + ","
                                + "\"status\":\"ACTIVE\""
                                + "}"))
                .andExpect(status().isCreated());

        // SUBS-03: Get subscriptions for user
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/subscriptions/user/" + userId).header("Authorization",
                "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());

        // SUBS-04: Get subscriptions by status
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/subscriptions/status").param("status", "ACTIVE")
                .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void searchByUserNameSuccess() throws Exception {
        // Create subscription
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/subscriptions")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + adminToken)
                .content(
                        "{"
                                + "\"operatorId\":" + operatorId + ","
                                + "\"planId\":" + planId + ","
                                + "\"userId\":" + userId + ","
                                + "\"status\":\"ACTIVE\""
                                + "}"))
                .andExpect(status().isCreated());

        // SUB-REPO-01: Find by user name
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/subscriptions/search/userName").param("name", "User")
                .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void findByUserIdAndStatusSuccess() throws Exception {
        // Create subscription
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/subscriptions")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + adminToken)
                .content(
                        "{"
                                + "\"operatorId\":" + operatorId + ","
                                + "\"planId\":" + planId + ","
                                + "\"userId\":" + userId + ","
                                + "\"status\":\"ACTIVE\""
                                + "}"))
                .andExpect(status().isCreated());

        // SUB-REPO-02: Find by user id and status
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/subscriptions/user/" + userId + "/status")
                .param("status", "ACTIVE").header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void countSuccess() throws Exception {
        // Create subscription
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/subscriptions")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + adminToken)
                .content(
                        "{"
                                + "\"operatorId\":" + operatorId + ","
                                + "\"planId\":" + planId + ","
                                + "\"userId\":" + userId + ","
                                + "\"status\":\"ACTIVE\""
                                + "}"))
                .andExpect(status().isCreated());

        // SUB-REPO-03: Count subscriptions
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/subscriptions/count").header("Authorization",
                "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isNumber());
    }
}
