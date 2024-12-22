package org.noisevisionproductions.portfolio.auth.service;

import lombok.RequiredArgsConstructor;
import org.noisevisionproductions.portfolio.auth.component.CustomAuthenticationProvider;
import org.noisevisionproductions.portfolio.auth.dto.AuthResponse;
import org.noisevisionproductions.portfolio.auth.dto.LoginRequest;
import org.noisevisionproductions.portfolio.auth.exceptions.InvalidCredentialsException;
import org.noisevisionproductions.portfolio.auth.model.UserModel;
import org.noisevisionproductions.portfolio.auth.security.JwtService;
import org.noisevisionproductions.portfolio.kafka.event.dto.LoginAttemptEvent;
import org.noisevisionproductions.portfolio.kafka.event.model.EventStatus;
import org.noisevisionproductions.portfolio.kafka.service.producer.LoginEventProducer;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class LoginService {
    private final JwtService jwtService;
    private final CustomAuthenticationProvider authenticationProvider;
    private final LoginEventProducer loginEventProducer;

    public AuthResponse login(LoginRequest loginRequest) {
        try {
            Authentication authentication = authenticationProvider.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.email(),
                            loginRequest.password()
                    )
            );

            UserModel user = (UserModel) authentication.getPrincipal();
            String token = jwtService.generateToken(user);

            loginEventProducer.sendEvent(LoginAttemptEvent.builder()
                    .userId(user.getId().toString())
                    .email(user.getEmail())
                    .status(EventStatus.SUCCESS)
                    .loginTime(LocalDateTime.now())
                    .build());

            return buildAuthResponse(user, token);
        } catch (AuthenticationException e) {
            loginEventProducer.sendEvent(LoginAttemptEvent.builder()
                    .email(loginRequest.email())
                    .status(EventStatus.FAILED)
                    .failureReason("INVALID_CREDENTIALS")
                    .loginTime(LocalDateTime.now())
                    .build());

            throw new InvalidCredentialsException("Invalid email or password");
        }
    }

    private AuthResponse buildAuthResponse(UserModel userModel, String token) {
        return new AuthResponse(
                token,
                userModel.getEmail(),
                userModel.getRole().name(),
                userModel.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .collect(Collectors.toSet())
        );
    }
}
