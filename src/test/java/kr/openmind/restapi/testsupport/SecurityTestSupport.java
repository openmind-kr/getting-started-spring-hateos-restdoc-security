package kr.openmind.restapi.testsupport;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.openmind.restapi.account.Account;
import kr.openmind.restapi.account.AccountRole;
import kr.openmind.restapi.account.AccountService;
import kr.openmind.restapi.common.ApplicationSecurityProperties;
import org.assertj.core.internal.bytebuddy.utility.RandomString;
import org.mockito.internal.util.collections.Sets;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.stereotype.Component;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Component
public class SecurityTestSupport {

    private final MockMvc mockMvc;
    private final AccountService accountService;
    private final ObjectMapper objectMapper;
    private final ApplicationSecurityProperties applicationSecurityProperties;

    public SecurityTestSupport(MockMvc mockMvc,
                               AccountService accountService,
                               ObjectMapper objectMapper,
                               ApplicationSecurityProperties applicationSecurityProperties) {
        this.mockMvc = mockMvc;
        this.accountService = accountService;
        this.objectMapper = objectMapper;
        this.applicationSecurityProperties = applicationSecurityProperties;
    }

    public String getBearerAccessToken() throws Exception {
        return "Bearer ".concat(getAccessToken());
    }

    private String getAccessToken() throws Exception {
        String email = "user" + RandomString.make() + "@email.com";
        String password = "password"; // any

        Account account = new Account();
        account.setEmail(email);
        account.setPassword(password); // any
        account.setRoles(Sets.newSet(AccountRole.USER));

        Account savedAccount = accountService.createAccount(account);
        return getAccessToken(savedAccount, password);
    }

    private String getAccessToken(Account savedAccount, String password) throws Exception {
        MultiValueMap<String, String> params = new LinkedMultiValueMap();
        params.add("grant_type", "password");
        params.add("username", savedAccount.getEmail());
        params.add("password", password);

        String defaultClientId = applicationSecurityProperties.getDefaultClientId();
        String defaultClientSecret = applicationSecurityProperties.getDefaultClientSecret();
        ResultActions resultActions = mockMvc
            .perform(post("/oauth/token")
                         .params(params)
                         .with(SecurityMockMvcRequestPostProcessors.httpBasic(defaultClientId, defaultClientSecret))
                         .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
            .andExpect(jsonPath("access_token").isNotEmpty())
            .andExpect(jsonPath("token_type").value("bearer"))
            .andExpect(jsonPath("refresh_token").isNotEmpty())
            .andExpect(jsonPath("expires_in").isNumber())
            .andExpect(jsonPath("scope").value("read write trust"));

        String contentAsString = resultActions.andReturn().getResponse().getContentAsString();
        return objectMapper.readValue(contentAsString, Map.class).get("access_token").toString();
    }
}
