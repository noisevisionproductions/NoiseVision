package org.noisevisionproductions.noisevision.intergration.kafka;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import org.noisevisionproductions.noisevision.auth.security.JwtAuthFilter;
import org.noisevisionproductions.noisevision.auth.security.JwtService;
import org.noisevisionproductions.noisevision.intergration.config.KafkaTestConfig;
import org.noisevisionproductions.noisevision.intergration.config.SecurityConfigTest;
import org.noisevisionproductions.noisevision.kafka.controller.LoginStatsController;
import org.noisevisionproductions.noisevision.kafka.stats.LoginStatsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.noisevisionproductions.noisevision.auth.component.CustomAuthenticationProvider;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(LoginStatsController.class)
@Import({SecurityConfigTest.class, KafkaTestConfig.class})
@AutoConfigureMockMvc
public class LoginStatsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private LoginStatsService statsService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private JwtAuthFilter jwtAuthFilter;

    @MockitoBean
    private CustomAuthenticationProvider customAuthenticationProvider;

    @BeforeEach
    void setUp() throws Exception {
        doAnswer(invocation -> {
            ServletRequest request = invocation.getArgument(0);
            ServletResponse response = invocation.getArgument(1);
            FilterChain chain = invocation.getArgument(2);
            chain.doFilter(request, response);
            return null;
        }).when(jwtAuthFilter).doFilter(any(), any(), any());
    }

    @Test
    void shouldReturnUnauthorizedWhenNoAuthentication() throws Exception {
        mockMvc.perform(get("/api/kafka/stats/logins"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "test-user", authorities = "ACCESS_KAFKA_DASHBOARD")
    void shouldReturnOkWhenAuthenticated() throws Exception {
        mockMvc.perform(get("/api/kafka/stats/logins"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(authorities = "USER")
    void shouldReturnForbiddenForNonAdminUser() throws Exception {
        mockMvc.perform(get("/api/kafka/stats/logins"))
                .andExpect(status().isForbidden());
    }
}
