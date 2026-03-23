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
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import se.lexicon.subscriptionapi.repository.RequestRepository;

@SpringBootTest
class ChangeRequestFlowHttpMatrixTest {
    @Autowired
    private WebApplicationContext context;

    @Autowired
    private FilterChainProxy springSecurityFilterChain;

    @Autowired
    private RequestRepository requestRepository;

    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private String suffix;

    @BeforeEach
    void setup() {
        suffix = String.valueOf(System.nanoTime());
        requestRepository.deleteAll();
        mockMvc = MockMvcBuilders.webAppContextSetup(context).addFilters(springSecurityFilterChain).build();
    }

    @Test
    void changeRequestFlow_operatorSubmits_adminReviews_operatorListsOwn() throws Exception {
        String adminEmail = "admin_" + suffix + "@example.com";
        String operatorEmail = "operator_" + suffix + "@example.com";
        String userEmail = "user_" + suffix + "@example.com";
        String password = "Password123!";

        Long userId = registerUser("ROLE_USER", userEmail, "User" + suffix, password, "+46123456789");
        registerUser("ROLE_OPERATOR", operatorEmail, "Operator" + suffix, password, "+46111222333");
        registerUser("ROLE_ADMIN", adminEmail, "Admin" + suffix, password, "+46100000001");

        String operatorToken = loginAndExtractToken(operatorEmail, password);
        String adminToken = loginAndExtractToken(adminEmail, password);

        MvcResult createdOperator = mockMvc.perform(withBearer(
                MockMvcRequestBuilders.post("/api/v1/operators")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"countryCode\":\"SE\",\"name\":\"SeedOperator" + suffix + "\"}"),
                adminToken))
                .andExpect(status().isCreated())
                .andReturn();
        Long operatorEntityId = objectMapper.readTree(createdOperator.getResponse().getContentAsString()).get("id")
                .asLong();
        Long planId = 1L;

        Long createPlanRequestId = submitPlanChangeRequest(
                operatorToken,
                "{"
                        + "\"action\":\"CREATE\","
                        + "\"name\":\"PlanByChangeRequest" + suffix + "\","
                        + "\"kind\":\"INTERNET\","
                        + "\"price\":123.45,"
                        + "\"status\":\"ACTIVE\","
                        + "\"uploadSpeedMbps\":50,"
                        + "\"downloadSpeedMbps\":200"
                        + "}");

        submitPlanChangeRequest(
                operatorToken,
                "{"
                        + "\"action\":\"UPDATE\","
                        + "\"planId\":" + planId + ","
                        + "\"kind\":\"INTERNET\","
                        + "\"name\":\"PlanSeedUpdatedByRequest" + suffix + "\","
                        + "\"price\":219.00,"
                        + "\"status\":\"ACTIVE\","
                        + "\"uploadSpeedMbps\":120,"
                        + "\"downloadSpeedMbps\":550"
                        + "}");

        executeAndExtractId(
                withBearer(MockMvcRequestBuilders.delete("/api/v1/operators/requests/plans/{planId}", planId),
                        operatorToken),
                status().isCreated());

        executeAndExtractId(
                withBearer(
                        MockMvcRequestBuilders.put("/api/v1/operators/requests/operator/{operatorId}", operatorEntityId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        "{"
                                                + "\"action\":\"UPDATE\","
                                                + "\"name\":\"OperatorEntityUpdatedByRequest" + suffix + "\","
                                                + "\"countryCode\":\"SE\""
                                                + "}"),
                        operatorToken),
                status().isCreated());

        executeAndExtractId(
                withBearer(
                        MockMvcRequestBuilders.post("/api/v1/operators/requests/subscriptions")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        "{"
                                                + "\"action\":\"CREATE\","
                                                + "\"planId\":" + planId + ","
                                                + "\"userId\":" + userId + "}"),
                        operatorToken),
                status().isCreated());

        mockMvc.perform(
                withBearer(MockMvcRequestBuilders.get("/api/v1/admin/requests").param("status", "PENDING"), adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").exists());

        mockMvc.perform(withBearer(
                MockMvcRequestBuilders.post("/api/v1/admin/requests/{id}/reject", createPlanRequestId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"reason\":\"Not allowed for test\"}"),
                adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(createPlanRequestId));

        mockMvc.perform(withBearer(MockMvcRequestBuilders.get("/api/v1/operators/requests/mine"), operatorToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").exists());
    }

    private Long registerUser(String role, String email, String firstName, String password, String phone)
            throws Exception {
        String payload = "{"
                + "\"id\":null,"
                + "\"credentials\":\"" + role + "\","
                + "\"email\":\"" + email + "\","
                + "\"firstName\":\"" + firstName + "\","
                + "\"lastName\":\"Tester\","
                + "\"password\":\"" + password + "\","
                + "\"address\":\"Test Street 1\","
                + "\"phoneNumber\":\"" + phone + "\","
                + "\"preferences\":\"http-matrix\""
                + "}";

        MvcResult register = mockMvc
                .perform(MockMvcRequestBuilders.post("/api/v1/auth/register").contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isOk())
                .andReturn();
        return objectMapper.readTree(register.getResponse().getContentAsString()).get("id").asLong();
    }

    private String loginAndExtractToken(String email, String password) throws Exception {
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"" + email + "\",\"password\":\"" + password + "\"}"))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode json = objectMapper.readTree(result.getResponse().getContentAsString());
        return json.get("accessToken").asText();
    }

    private Long submitPlanChangeRequest(String operatorToken, String payload) throws Exception {
        return executeAndExtractId(
                withBearer(MockMvcRequestBuilders.post("/api/v1/operators/requests/plans")
                        .contentType(MediaType.APPLICATION_JSON).content(payload), operatorToken),
                status().isCreated());
    }

    private MockHttpServletRequestBuilder withBearer(MockHttpServletRequestBuilder builder, String token) {
        return builder.header("Authorization", "Bearer " + token);
    }

    private Long executeAndExtractId(MockHttpServletRequestBuilder request, ResultMatcher expectedStatus)
            throws Exception {
        MvcResult result = mockMvc.perform(request).andExpect(expectedStatus).andExpect(jsonPath("$.id").exists())
                .andReturn();
        JsonNode json = objectMapper.readTree(result.getResponse().getContentAsString());
        return json.get("id").asLong();
    }
}
