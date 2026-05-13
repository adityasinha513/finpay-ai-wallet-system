package com.finpay.backend.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.finpay.backend.analytics.dto.AIChatRequest;
import com.finpay.backend.analytics.dto.AIChatResponse;
import com.finpay.backend.analytics.service.LLMFinancialAgentService;
import com.finpay.backend.auth.dto.LoginRequest;
import com.finpay.backend.auth.dto.RegisterRequest;
import com.finpay.backend.auth.entity.User;
import com.finpay.backend.auth.enums.Role;
import com.finpay.backend.auth.repository.UserRepository;
import com.finpay.backend.transaction.enums.TransactionType;
import com.finpay.backend.wallet.dto.CreditWalletRequest;
import com.finpay.backend.wallet.dto.KycVerificationRequest;
import com.finpay.backend.wallet.dto.SetTransactionPinRequest;
import com.finpay.backend.wallet.repository.WalletRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class FinpayFullApiIntegrationTest {

    private static final String ADMIN_EMAIL = "admin-api-test@finpay.local";

    private static final String PASSWORD = "TestPassword1!";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private WalletRepository walletRepository;

    @MockBean
    private LLMFinancialAgentService llmFinancialAgentService;

    @BeforeEach
    void stubLlm() {

        when(llmFinancialAgentService.chat(any(), any()))
                .thenReturn(new AIChatResponse("stubbed-ai-response"));
    }

    @BeforeEach
    void seedAdmin() {

        if (userRepository.existsByEmail(ADMIN_EMAIL)) {
            return;
        }

        User admin = new User();

        admin.setName("Admin");
        admin.setEmail(ADMIN_EMAIL);
        admin.setPassword(passwordEncoder.encode(PASSWORD));
        admin.setRole(Role.ADMIN);

        userRepository.save(admin);
    }

    private static String uniqueEmail(String prefix) {

        return prefix + "-" + UUID.randomUUID() + "@finpay.local";
    }

    @Test
    void healthAndActuatorHealthArePublic() throws Exception {

        mockMvc.perform(get("/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        mockMvc.perform(get("/actuator/health"))
                .andExpect(status().isOk());
    }

    @Test
    void swaggerUiEntrypointIsPublic() throws Exception {

        mockMvc.perform(get("/swagger-ui.html"))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    void protectedEndpointWithoutTokenReturns401() throws Exception {

        mockMvc.perform(get("/api/v1/wallet"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void registerValidationFailsForInvalidEmail() throws Exception {

        RegisterRequest bad = new RegisterRequest();

        bad.setName("X");
        bad.setEmail("not-an-email");
        bad.setPassword("secret");

        mockMvc.perform(
                        post("/api/v1/auth/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(bad))
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    void loginBadCredentialsReturns401() throws Exception {

        String email = uniqueEmail("loginbad");

        register(email, "User One");

        LoginRequest login = new LoginRequest();

        login.setEmail(email);
        login.setPassword("wrong-password");

        mockMvc.perform(
                        post("/api/v1/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(login))
                )
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    void duplicateRegisterReturns409() throws Exception {

        String email = uniqueEmail("dup");

        register(email, "User One");

        RegisterRequest again = new RegisterRequest();

        again.setName("Dup");
        again.setEmail(email);
        again.setPassword(PASSWORD);

        mockMvc.perform(
                        post("/api/v1/auth/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(again))
                )
                .andExpect(status().isConflict());
    }

    @Test
    void userCannotAccessAdminEndpoint() throws Exception {

        String email = uniqueEmail("useronly");

        register(email, "User One");

        String token = login(email);

        mockMvc.perform(
                        get("/api/v1/admin/test")
                                .header("Authorization", "Bearer " + token)
                )
                .andExpect(status().isForbidden());
    }

    @Test
    void adminCanAccessAdminEndpoint() throws Exception {

        String token = login(ADMIN_EMAIL);

        mockMvc.perform(
                        get("/api/v1/admin/test")
                                .header("Authorization", "Bearer " + token)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void walletFlowTransferHistoryAndPaginationValidation() throws Exception {

        String u1 = uniqueEmail("flow1");
        String u2 = uniqueEmail("flow2");

        register(u1, "User One");
        register(u2, "User Two");

        String token1 = login(u1);

        String receiverWalletNumber = walletRepository
                .findByUserId(
                        userRepository.findByEmail(u2).orElseThrow().getId()
                )
                .orElseThrow()
                .getWalletNumber();

        mockMvc.perform(
                        post("/api/v1/wallet/credit")
                                .header("Authorization", "Bearer " + token1)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(credit(50_000)))
                )
                .andExpect(status().isOk());

        mockMvc.perform(
                        put("/api/v1/wallet/set-pin")
                                .header("Authorization", "Bearer " + token1)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(pin("1234")))
                )
                .andExpect(status().isOk());

        mockMvc.perform(
                        put("/api/v1/wallet/complete-kyc")
                                .header("Authorization", "Bearer " + token1)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(kyc()))
                )
                .andExpect(status().isOk());

        Map<String, Object> transfer = Map.of(
                "receiverWalletNumber",
                receiverWalletNumber,
                "amount",
                new BigDecimal("100.00"),
                "transactionPin",
                "1234",
                "description",
                "api-test"
        );

        mockMvc.perform(
                        post("/api/v1/transactions/transfer")
                                .header("Authorization", "Bearer " + token1)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(transfer))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.referenceId").exists());

        mockMvc.perform(
                        get("/api/v1/transactions")
                                .header("Authorization", "Bearer " + token1)
                                .param("page", "0")
                                .param("size", "10")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content").isArray());

        mockMvc.perform(
                        get("/api/v1/transactions/type/" + TransactionType.TRANSFER.name())
                                .header("Authorization", "Bearer " + token1)
                )
                .andExpect(status().isOk());

        mockMvc.perform(
                        get("/api/v1/transactions")
                                .header("Authorization", "Bearer " + token1)
                                .param("page", "-1")
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    void transferInsufficientBalanceReturns400() throws Exception {

        String u1 = uniqueEmail("insuf1");
        String u2 = uniqueEmail("insuf2");

        register(u1, "User One");
        register(u2, "User Two");

        String token1 = login(u1);

        String receiverWalletNumber = walletRepository
                .findByUserId(
                        userRepository.findByEmail(u2).orElseThrow().getId()
                )
                .orElseThrow()
                .getWalletNumber();

        mockMvc.perform(
                        put("/api/v1/wallet/set-pin")
                                .header("Authorization", "Bearer " + token1)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(pin("9999")))
                )
                .andExpect(status().isOk());

        mockMvc.perform(
                        put("/api/v1/wallet/complete-kyc")
                                .header("Authorization", "Bearer " + token1)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(kyc()))
                )
                .andExpect(status().isOk());

        Map<String, Object> transfer = Map.of(
                "receiverWalletNumber",
                receiverWalletNumber,
                "amount",
                new BigDecimal("999999.00"),
                "transactionPin",
                "9999"
        );

        mockMvc.perform(
                        post("/api/v1/transactions/transfer")
                                .header("Authorization", "Bearer " + token1)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(transfer))
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    void transferWithoutKycReturns400() throws Exception {

        String u1 = uniqueEmail("nokyc1");
        String u2 = uniqueEmail("nokyc2");

        register(u1, "User One");
        register(u2, "User Two");

        String token1 = login(u1);

        String receiverWalletNumber = walletRepository
                .findByUserId(
                        userRepository.findByEmail(u2).orElseThrow().getId()
                )
                .orElseThrow()
                .getWalletNumber();

        mockMvc.perform(
                        post("/api/v1/wallet/credit")
                                .header("Authorization", "Bearer " + token1)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(credit(10_000)))
                )
                .andExpect(status().isOk());

        mockMvc.perform(
                        put("/api/v1/wallet/set-pin")
                                .header("Authorization", "Bearer " + token1)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(pin("3333")))
                )
                .andExpect(status().isOk());

        Map<String, Object> transfer = Map.of(
                "receiverWalletNumber",
                receiverWalletNumber,
                "amount",
                new BigDecimal("10.00"),
                "transactionPin",
                "3333"
        );

        mockMvc.perform(
                        post("/api/v1/transactions/transfer")
                                .header("Authorization", "Bearer " + token1)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(transfer))
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    void transferWrongPinReturns400() throws Exception {

        String u1 = uniqueEmail("pin1");
        String u2 = uniqueEmail("pin2");

        register(u1, "User One");
        register(u2, "User Two");

        String token1 = login(u1);

        String receiverWalletNumber = walletRepository
                .findByUserId(
                        userRepository.findByEmail(u2).orElseThrow().getId()
                )
                .orElseThrow()
                .getWalletNumber();

        mockMvc.perform(
                        post("/api/v1/wallet/credit")
                                .header("Authorization", "Bearer " + token1)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(credit(10_000)))
                )
                .andExpect(status().isOk());

        mockMvc.perform(
                        put("/api/v1/wallet/set-pin")
                                .header("Authorization", "Bearer " + token1)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(pin("1111")))
                )
                .andExpect(status().isOk());

        mockMvc.perform(
                        put("/api/v1/wallet/complete-kyc")
                                .header("Authorization", "Bearer " + token1)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(kyc()))
                )
                .andExpect(status().isOk());

        Map<String, Object> transfer = Map.of(
                "receiverWalletNumber",
                receiverWalletNumber,
                "amount",
                new BigDecimal("10.00"),
                "transactionPin",
                "2222"
        );

        mockMvc.perform(
                        post("/api/v1/transactions/transfer")
                                .header("Authorization", "Bearer " + token1)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(transfer))
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    void analyticsSummaryRequiresAuth() throws Exception {

        mockMvc.perform(get("/api/v1/analytics/summary"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void analyticsSummaryWithAuthReturns200() throws Exception {

        String email = uniqueEmail("analytics");

        register(email, "User One");

        String token = login(email);

        mockMvc.perform(
                        get("/api/v1/analytics/summary")
                                .header("Authorization", "Bearer " + token)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void agentChatWithStubbedLlmReturns200() throws Exception {

        String email = uniqueEmail("chat");

        register(email, "User One");

        String token = login(email);

        AIChatRequest req = new AIChatRequest();

        req.setPrompt("show my financial summary");

        mockMvc.perform(
                        post("/api/v1/analytics/agent/chat")
                                .header("Authorization", "Bearer " + token)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(req))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.response").value("stubbed-ai-response"));
    }

    private void register(String email, String name) throws Exception {

        RegisterRequest r = new RegisterRequest();

        r.setName(name);
        r.setEmail(email);
        r.setPassword(PASSWORD);

        mockMvc.perform(
                        post("/api/v1/auth/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(r))
                )
                .andExpect(status().isOk());
    }

    private String login(String email) throws Exception {

        LoginRequest login = new LoginRequest();

        login.setEmail(email);
        login.setPassword(PASSWORD);

        String body = mockMvc.perform(
                        post("/api/v1/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(login))
                )
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        return objectMapper.readTree(body)
                .path("data")
                .path("token")
                .asText();
    }

    private static CreditWalletRequest credit(double amount) {

        CreditWalletRequest c = new CreditWalletRequest();

        c.setAmount(BigDecimal.valueOf(amount));

        return c;
    }

    private static SetTransactionPinRequest pin(String p) {

        SetTransactionPinRequest s = new SetTransactionPinRequest();

        s.setTransactionPin(p);

        return s;
    }

    private static KycVerificationRequest kyc() {

        KycVerificationRequest k = new KycVerificationRequest();

        k.setPanNumber("ABCDE1234F");
        k.setAadhaarMasked("XXXX-XXXX-5678");

        return k;
    }
}
