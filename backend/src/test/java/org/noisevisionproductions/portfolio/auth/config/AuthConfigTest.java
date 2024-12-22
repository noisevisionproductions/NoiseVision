package org.noisevisionproductions.portfolio.auth.config;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.noisevisionproductions.portfolio.auth.service.CustomUserDetailsService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthConfigTest {

    @Mock
    private CustomUserDetailsService userDetailsService;

    @Mock
    private AuthenticationConfiguration authenticationConfiguration;

    @InjectMocks
    private AuthConfig authConfig;


    @Test
    void authenticationProvider_ShouldReturnDaoAuthenticationProvider() {
        UserDetails userDetails = new User("testUser", "encodedPassword", Collections.emptyList());
        when(userDetailsService.loadUserByUsername("testUser")).thenReturn(userDetails);

        AuthenticationProvider result = authConfig.authenticationProvider();

        assertInstanceOf(DaoAuthenticationProvider.class, result);

        try {
            result.authenticate(new UsernamePasswordAuthenticationToken("testUser", "testPassword"));
        } catch (Exception e) {
            verify(userDetailsService).loadUserByUsername("testUser");
        }
    }

    @Test
    void passwordEncoderAuth_ShouldReturnBCryptPasswordEncoder() {
        PasswordEncoder result = authConfig.passwordEncoderAuth();

        assertInstanceOf(BCryptPasswordEncoder.class, result);

        String password = "testPassword";
        String encoded = result.encode(password);
        assertTrue(result.matches(password, encoded));
    }

    @Test
    void authenticationManager_ShouldReturnAuthenticationManagerFromConfiguration() throws Exception {
        AuthenticationManager expectedManager = mock(AuthenticationManager.class);
        when(authenticationConfiguration.getAuthenticationManager()).thenReturn(expectedManager);

        AuthenticationManager result = authConfig.authenticationManager(authenticationConfiguration);

        assertSame(expectedManager, result);
        verify(authenticationConfiguration).getAuthenticationManager();
    }
}