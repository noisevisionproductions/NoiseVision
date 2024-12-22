package org.noisevisionproductions.portfolio.auth.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.noisevisionproductions.portfolio.auth.component.CustomAuthenticationProvider;
import org.noisevisionproductions.portfolio.auth.dto.AuthResponse;
import org.noisevisionproductions.portfolio.auth.dto.LoginRequest;
import org.noisevisionproductions.portfolio.auth.exceptions.InvalidCredentialsException;
import org.noisevisionproductions.portfolio.auth.model.UserModel;
import org.noisevisionproductions.portfolio.auth.model.enums.Role;
import org.noisevisionproductions.portfolio.auth.security.JwtService;
import org.noisevisionproductions.portfolio.kafka.event.dto.LoginAttemptEvent;
import org.noisevisionproductions.portfolio.kafka.event.model.EventStatus;
import org.noisevisionproductions.portfolio.kafka.service.producer.LoginEventProducer;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoginServiceTest {

    @Mock
    private JwtService jwtService;

    @Mock
    private LoginEventProducer loginEventProducer;

    @Mock
    private CustomAuthenticationProvider customAuthenticationProvider;

    @InjectMocks
    private LoginService baseLoginService;

    @Test
    void login_ShouldReturnAuthResponse_WhenCredentialsAreValid() {
        LoginRequest loginRequest = new LoginRequest("test@example.com", "password123");
        UserModel userModel = new UserModel();
        userModel.setId(1L);
        userModel.setEmail(loginRequest.email());
        userModel.setRole(Role.USER);

        Authentication successfulAuth = new UsernamePasswordAuthenticationToken(
                userModel,
                loginRequest.password(),
                userModel.getAuthorities()
        );

        String generatedToken = "generatedToken123";

        when(customAuthenticationProvider.authenticate(any(Authentication.class)))
                .thenReturn(successfulAuth);
        when(jwtService.generateToken(userModel)).thenReturn(generatedToken);
        doNothing().when(loginEventProducer).sendEvent(any(LoginAttemptEvent.class));

        AuthResponse response = baseLoginService.login(loginRequest);

        assertThat(response).isNotNull();
        assertThat(response.token()).isEqualTo(generatedToken);
        assertThat(response.email()).isEqualTo(loginRequest.email());
        assertThat(response.role()).isEqualTo(Role.USER.name());
        assertThat(response.authorities()).contains("ROLE_USER");

        verify(customAuthenticationProvider).authenticate(
                argThat(auth ->
                        auth.getPrincipal().equals(loginRequest.email()) &&
                                auth.getCredentials().equals(loginRequest.password())
                )
        );
        verify(jwtService).generateToken(userModel);
        verify(loginEventProducer).sendEvent(argThat(event ->
                event.getEmail().equals(loginRequest.email()) &&
                        event.getUserId().equals("1") &&
                        event.getStatus() == EventStatus.SUCCESS
        ));
    }

    @Test
    void login_ShouldThrowException_WhenAuthenticationFails() {
        LoginRequest loginRequest = new LoginRequest("test@example.com", "wrongPassword");

        when(customAuthenticationProvider.authenticate(any(Authentication.class)))
                .thenThrow(new BadCredentialsException("Bad credentials"));
        doNothing().when(loginEventProducer).sendEvent(any(LoginAttemptEvent.class));

        assertThatThrownBy(() -> baseLoginService.login(loginRequest))
                .isInstanceOf(InvalidCredentialsException.class)
                .hasMessage("Invalid email or password");

        verify(customAuthenticationProvider).authenticate(
                argThat(auth ->
                        auth.getPrincipal().equals(loginRequest.email()) &&
                                auth.getCredentials().equals(loginRequest.password())
                )
        );
        verify(loginEventProducer).sendEvent(argThat(event ->
                event.getEmail().equals(loginRequest.email()) &&
                        event.getStatus() == EventStatus.FAILED &&
                        event.getFailureReason().equals("INVALID_CREDENTIALS")
        ));
        verifyNoInteractions(jwtService);
    }
}