package org.noisevisionproductions.noisevision.auth.service;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.noisevisionproductions.noisevision.auth.component.IpAddressExtractor;
import org.noisevisionproductions.noisevision.auth.dto.AuthResponse;
import org.noisevisionproductions.noisevision.auth.dto.RegisterRequest;
import org.noisevisionproductions.noisevision.auth.exceptions.EmailAlreadyExistsException;
import org.noisevisionproductions.noisevision.auth.model.UserModel;
import org.noisevisionproductions.noisevision.auth.model.enums.Role;
import org.noisevisionproductions.noisevision.auth.repository.UserRepository;
import org.noisevisionproductions.noisevision.auth.security.JwtService;
import org.noisevisionproductions.noisevision.kafka.event.dto.UserRegistrationEvent;
import org.noisevisionproductions.noisevision.kafka.event.model.EventStatus;
import org.noisevisionproductions.noisevision.kafka.service.producer.RegistrationEventProducer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class RegistrationService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final SuccessfulRegistrationService registrationRateLimiter;
    private final RegistrationEventProducer registrationEventProducer;
    private final IpAddressExtractor ipAddressExtractor;

    public AuthResponse register(RegisterRequest registerRequest, HttpServletRequest request) {
        String ipAddress = ipAddressExtractor.getClientIpAddress(request);
        registrationRateLimiter.canRegister(ipAddress);

        try {
            if (userRepository.existsByEmail(registerRequest.email())) {
                publishRegistrationEvent(registerRequest, ipAddress, request.getHeader("User-Agent"));
                throw new EmailAlreadyExistsException();
            }

            UserModel savedUser = createAndSaveUser(registerRequest);
            registrationRateLimiter.registerSuccessfulRegistration(ipAddress);
            publishRegistrationEvent(savedUser, ipAddress, request.getHeader("User-Agent"));

            String token = jwtService.generateToken(savedUser);
            return buildAuthResponse(savedUser, token);
        } catch (EmailAlreadyExistsException e) {
            throw e;
        } catch (Exception e) {
            publishRegistrationEvent(registerRequest, ipAddress, request.getHeader("User-Agent"));
            throw e;
        }
    }

    private UserModel createAndSaveUser(RegisterRequest request) {
        UserModel userModel = new UserModel();
        userModel.setEmail(request.email());
        userModel.setPassword(passwordEncoder.encode(request.password()));
        userModel.setName(request.name());
        userModel.setCompanyName(request.companyName());
        userModel.setProgrammingLanguages(request.programmingLanguages());
        userModel.setRole(Role.USER);

        return userRepository.save(userModel);
    }

    private void publishRegistrationEvent(UserModel user, String ipAddress, String userAgent) {
        UserRegistrationEvent event = UserRegistrationEvent.builder()
                .eventId(UUID.randomUUID().toString())
                .eventType("USER_REGISTRATION")
                .timestamp(LocalDateTime.now())
                .userId(user.getId().toString())
                .email(user.getEmail())
                .name(user.getName())
                .companyName(user.getCompanyName())
                .status(EventStatus.SUCCESS)
                .registrationTime(LocalDateTime.now())
                .ipAddress(ipAddress)
                .userAgent(userAgent)
                .registrationSource("WEB")
                .build();

        registrationEventProducer.sendEvent(event);
    }

    private void publishRegistrationEvent(RegisterRequest request, String ipAddress, String userAgent) {
        UserRegistrationEvent event = UserRegistrationEvent.builder()
                .email(request.email())
                .name(request.name())
                .companyName(request.companyName())
                .timestamp(LocalDateTime.now())
                .status(EventStatus.FAILED)
                .registrationTime(LocalDateTime.now())
                .ipAddress(ipAddress)
                .userAgent(userAgent)
                .registrationSource("WEB")
                .eventId(UUID.randomUUID().toString())
                .eventType("USER_REGISTRATION")
                .build();

        registrationEventProducer.sendEvent(event);
    }

    private AuthResponse buildAuthResponse(UserModel user, String token) {
        return new AuthResponse(
                token,
                user.getEmail(),
                user.getRole().name(),
                user.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .collect(Collectors.toSet())
        );
    }
}