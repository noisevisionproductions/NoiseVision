package org.noisevisionproductions.noisevision.security;

import org.junit.jupiter.api.Test;
import org.noisevisionproductions.noisevision.auth.component.CustomAuthenticationProvider;
import org.noisevisionproductions.noisevision.auth.repository.UserRepository;
import org.noisevisionproductions.noisevision.auth.security.JwtAuthFilter;
import org.noisevisionproductions.noisevision.auth.security.JwtService;
import org.noisevisionproductions.noisevision.auth.service.SuccessfulRegistrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SecurityConfig.class)
@AutoConfigureMockMvc
class SecurityConfigTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private JwtAuthFilter jwtAuthFilter;

    @MockitoBean
    private CustomAuthenticationProvider customAuthenticationProvider;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private UserRepository userRepository;

    @MockitoBean
    private SuccessfulRegistrationService registrationService;

    @Test
    void publicEndpoints_ShouldBeAccessibleWithoutAuthentication() throws Exception {
        mockMvc.perform(post("/api/auth/register"))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/auth/login"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/projects"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/files/test"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/errors/test"))
                .andExpect(status().isOk());
    }

    @Test
    void cors_ShouldBeConfiguredCorrectly() throws Exception {
        mockMvc.perform(options("/api/projects")
                        .header("Origin", "http://localhost:3000")
                        .header("Access-Control-Request-Method", "GET"))
                .andExpect(status().isOk())
                .andExpect(header().exists("Access-Control-Allow-Origin"))
                .andExpect(header().string("Access-Control-Allow-Methods", containsString("GET")))
                .andExpect(header().exists("Access-Control-Max-Age"));
    }

    @Test
    void optionsRequest_ShouldBeAllowed() throws Exception {
        mockMvc.perform(options("/api/projects"))
                .andExpect(status().isOk());
    }
}