package org.noisevisionproductions.portfolio.auth.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetails;

import java.io.IOException;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtAuthFilterTest {

    @Mock
    private JwtService jwtService;

    @Mock
    private UserDetailsService userDetailsService;

    @Mock
    private FilterChain filterChain;

    @Mock
    private HttpServletResponse response;

    @InjectMocks
    private JwtAuthFilter jwtAuthFilter;

    private MockHttpServletRequest request;

    @BeforeEach
    void setUp() {
        request = new MockHttpServletRequest();
        SecurityContextHolder.clearContext();
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void shouldNotFilter_ShouldReturnTrue_ForPublicEndpoints() {
        request.setServletPath("/api/auth/login");
        assertThat(jwtAuthFilter.shouldNotFilter(request)).isTrue();

        request.setServletPath("/api/auth/register");
        assertThat(jwtAuthFilter.shouldNotFilter(request)).isTrue();

        request.setServletPath("/api/files/something");
        assertThat(jwtAuthFilter.shouldNotFilter(request)).isTrue();

        request.setServletPath("/api/projects");
        request.setMethod("GET");
        assertThat(jwtAuthFilter.shouldNotFilter(request)).isTrue();
    }

    @Test
    void shouldNotFilter_ShouldHandleException_WhenJwtServiceThrowsException() throws ServletException, IOException {
        String token = "valid.jwt.token";
        request.addHeader("Authorization", "Bearer " + token);

        when(jwtService.extractUsername(token)).thenThrow(new RuntimeException("JWT Error"));

        jwtAuthFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    @Test
    void doFilterInternal_ShouldSetAuthenticationDetails() throws ServletException, IOException {
        String token = "valid.jwt.token";
        String userEmail = "test@example.com";
        request.addHeader("Authorization", "Bearer " + token);

        UserDetails userDetails = User.builder()
                .username(userEmail)
                .password("password")
                .authorities(Collections.emptyList())
                .build();

        when(jwtService.extractUsername(token)).thenReturn(userEmail);
        when(userDetailsService.loadUserByUsername(userEmail)).thenReturn(userDetails);
        when(jwtService.isTokenValid(token, userDetails)).thenReturn(true);

        jwtAuthFilter.doFilterInternal(request, response, filterChain);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        assertThat(authentication.getDetails())
                .isNotNull()
                .isInstanceOf(WebAuthenticationDetails.class);
    }

    @Test
    void doFilterInternal_ShouldNotAuthenticate_WhenTokenIsTooShort() throws ServletException, IOException {
        request.addHeader("Authorization", "Bearer abc");
        when(jwtService.extractUsername("abc")).thenReturn(null);

        jwtAuthFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        verify(jwtService).extractUsername("abc");
        verifyNoMoreInteractions(jwtService);
        verifyNoInteractions(userDetailsService);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    @Test
    void doFilterInternal_ShouldSkipAuth_WhenNoAuthHeader() throws ServletException, IOException {
        jwtAuthFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        verifyNoInteractions(jwtService, userDetailsService);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    @Test
    void doFilterInternal_ShouldAuthUser_WhenValidToken() throws ServletException, IOException {
        String token = "valid.jwt.token";
        String userEmail = "test@example.com";
        request.addHeader("Authorization", "Bearer " + token);

        UserDetails userDetails = User.builder()
                .username(userEmail)
                .password("password")
                .authorities(Collections.emptyList())
                .build();

        when(jwtService.extractUsername(token)).thenReturn(userEmail);
        when(userDetailsService.loadUserByUsername(userEmail)).thenReturn(userDetails);
        when(jwtService.isTokenValid(token, userDetails)).thenReturn(true);

        jwtAuthFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        verify(jwtService).extractUsername(token);
        verify(userDetailsService).loadUserByUsername(userEmail);
        verify(jwtService).isTokenValid(token, userDetails);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        assertThat(authentication)
                .isNotNull()
                .isInstanceOf(UsernamePasswordAuthenticationToken.class);
        assertThat(authentication.getPrincipal()).isEqualTo(userDetails);
    }

    @Test
    void doFilterInternal_ShouldNotAuthenticate_WhenInvalidToken() throws ServletException, IOException {
        String token = "invalid.jwt.token";
        String userEmail = "test@example.com";
        request.addHeader("Authorization", "Bearer " + token);

        UserDetails userDetails = User.builder()
                .username(userEmail)
                .password("password")
                .authorities(Collections.emptyList())
                .build();

        when(jwtService.extractUsername(token)).thenReturn(userEmail);
        when(userDetailsService.loadUserByUsername(userEmail)).thenReturn(userDetails);
        when(jwtService.isTokenValid(token, userDetails)).thenReturn(false);

        jwtAuthFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        verify(jwtService).extractUsername(token);
        verify(userDetailsService).loadUserByUsername(userEmail);
        verify(jwtService).isTokenValid(token, userDetails);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    @Test
    void doFilterInternal_ShouldNotAuth_WhenUserAlreadyAuth() throws ServletException, IOException {
        String token = "valid.jwt.token";
        request.addHeader("Authorization", "Bearer " + token);

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("user", null, Collections.emptyList())
        );

        jwtAuthFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        verify(jwtService).extractUsername(token);
        verifyNoMoreInteractions(jwtService);
        verifyNoInteractions(userDetailsService);
    }

    @Test
    void doFilterInternal_ShouldHandleNullUsername() throws ServletException, IOException {
        String token = "invalid.jwt.token";
        request.addHeader("Authorization", "Bearer " + token);

        when(jwtService.extractUsername(token)).thenReturn(null);

        jwtAuthFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        verify(jwtService).extractUsername(token);
        verifyNoMoreInteractions(jwtService);
        verifyNoInteractions(userDetailsService);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }
}