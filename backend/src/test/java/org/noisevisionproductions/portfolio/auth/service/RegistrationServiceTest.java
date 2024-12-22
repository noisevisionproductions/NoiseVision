package org.noisevisionproductions.portfolio.auth.service;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.noisevisionproductions.portfolio.auth.component.IpAddressExtractor;
import org.noisevisionproductions.portfolio.auth.dto.AuthResponse;
import org.noisevisionproductions.portfolio.auth.dto.RegisterRequest;
import org.noisevisionproductions.portfolio.auth.exceptions.EmailAlreadyExistsException;
import org.noisevisionproductions.portfolio.auth.model.UserModel;
import org.noisevisionproductions.portfolio.auth.model.enums.Role;
import org.noisevisionproductions.portfolio.auth.repository.UserRepository;
import org.noisevisionproductions.portfolio.auth.security.JwtService;
import org.noisevisionproductions.portfolio.kafka.event.dto.UserRegistrationEvent;
import org.noisevisionproductions.portfolio.kafka.event.model.EventStatus;
import org.noisevisionproductions.portfolio.kafka.service.producer.RegistrationEventProducer;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RegistrationServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private SuccessfulRegistrationService successfulRegistrationService;

    @Mock
    private RegistrationEventProducer registrationEventProducer;

    @Mock
    private IpAddressExtractor ipAddressExtractor;

    @Mock
    private HttpServletRequest request;

    @InjectMocks
    private RegistrationService registrationService;

    @Test
    void register_ShouldCreateNewUser_WhenEmailDoesNotExist() {
        RegisterRequest registerRequest = new RegisterRequest(
                "test@example.com",
                "password123",
                "John Doe",
                "Tech Corp",
                Set.of("Java", "JavaScript")
        );

        String encodedPassword = "encodedPassword123";
        String generatedToken = "generatedToken123";
        String ipAddress = "127.0.0.1";

        when(ipAddressExtractor.getClientIpAddress(request)).thenReturn(ipAddress);
        when(userRepository.existsByEmail(registerRequest.email())).thenReturn(false);
        when(passwordEncoder.encode(registerRequest.password())).thenReturn(encodedPassword);
        when(jwtService.generateToken(any(UserModel.class))).thenReturn(generatedToken);

        UserModel savedUser = new UserModel();
        savedUser.setId(1L);
        savedUser.setEmail(registerRequest.email());
        savedUser.setPassword(encodedPassword);
        savedUser.setName(registerRequest.name());
        savedUser.setCompanyName(registerRequest.companyName());
        savedUser.setProgrammingLanguages(registerRequest.programmingLanguages());
        savedUser.setRole(Role.USER);
        when(userRepository.save(any(UserModel.class))).thenReturn(savedUser);

        AuthResponse response = registrationService.register(registerRequest, request);

        assertThat(response).isNotNull();
        assertThat(response.token()).isEqualTo(generatedToken);
        assertThat(response.email()).isEqualTo(registerRequest.email());

        verify(userRepository).existsByEmail(registerRequest.email());
        verify(passwordEncoder).encode(registerRequest.password());
        verify(userRepository).save(argThat(user ->
                user.getEmail().equals(registerRequest.email()) &&
                        user.getPassword().equals(encodedPassword) &&
                        user.getName().equals(registerRequest.name()) &&
                        user.getCompanyName().equals(registerRequest.companyName()) &&
                        user.getProgrammingLanguages().equals(registerRequest.programmingLanguages())
        ));
        verify(jwtService).generateToken(any(UserModel.class));
        verify(successfulRegistrationService).registerSuccessfulRegistration(ipAddress);
        verify(registrationEventProducer).sendEvent(argThat(event ->
                event.getStatus() == EventStatus.SUCCESS &&
                        event.getUserId().equals("1") &&
                        event.getEmail().equals(registerRequest.email())
        ));
    }

    @Test
    void register_ShouldThrowException_WhenEmailAlreadyExists() {
        RegisterRequest registerRequest = new RegisterRequest(
                "existing@example.com",
                "password123",
                "John Doe",
                "Tech Corp",
                Set.of("Java")
        );

        String ipAddress = "127.0.0.1";
        when(ipAddressExtractor.getClientIpAddress(request)).thenReturn(ipAddress);
        when(userRepository.existsByEmail(registerRequest.email())).thenReturn(true);

        ArgumentCaptor<UserRegistrationEvent> eventCaptor = ArgumentCaptor.forClass(UserRegistrationEvent.class);

        assertThatThrownBy(() -> registrationService.register(registerRequest, request))
                .isInstanceOf(EmailAlreadyExistsException.class);

        verify(successfulRegistrationService).canRegister(ipAddress);
        verify(userRepository).existsByEmail(registerRequest.email());
        verifyNoMoreInteractions(passwordEncoder, jwtService, userRepository);
        verify(successfulRegistrationService, never()).registerSuccessfulRegistration(ipAddress);

        verify(registrationEventProducer).sendEvent(eventCaptor.capture());
        UserRegistrationEvent capturedEvent = eventCaptor.getValue();
        assertThat(capturedEvent.getStatus()).isEqualTo(EventStatus.FAILED);
        assertThat(capturedEvent.getEmail()).isEqualTo(registerRequest.email());
        assertThat(capturedEvent.getName()).isEqualTo(registerRequest.name());
        assertThat(capturedEvent.getCompanyName()).isEqualTo(registerRequest.companyName());
        assertThat(capturedEvent.getUserId()).isNull();
    }

    @Test
    void register_ShouldHandleEmptyProgrammingLanguages() {
        RegisterRequest registerRequest = new RegisterRequest(
                "test@example.com",
                "password123",
                "John Doe",
                "Tech Corp",
                Collections.emptySet()
        );

        String encodedPassword = "encodedPassword123";
        String generatedToken = "generatedToken123";
        String ipAddress = "127.0.0.1";

        when(ipAddressExtractor.getClientIpAddress(request)).thenReturn(ipAddress);
        when(userRepository.existsByEmail(registerRequest.email())).thenReturn(false);
        when(passwordEncoder.encode(registerRequest.password())).thenReturn(encodedPassword);
        when(jwtService.generateToken(any(UserModel.class))).thenReturn(generatedToken);

        UserModel savedUser = new UserModel();
        savedUser.setId(1L);
        savedUser.setEmail(registerRequest.email());
        savedUser.setPassword(encodedPassword);
        savedUser.setProgrammingLanguages(Collections.emptySet());
        when(userRepository.save(any(UserModel.class))).thenReturn(savedUser);

        AuthResponse response = registrationService.register(registerRequest, request);

        assertThat(response).isNotNull();
        assertThat(response.token()).isEqualTo(generatedToken);

        verify(successfulRegistrationService).canRegister(ipAddress);
        verify(successfulRegistrationService).registerSuccessfulRegistration(ipAddress);
        verify(userRepository).save(argThat(user ->
                user.getProgrammingLanguages().isEmpty()
        ));
        verify(registrationEventProducer).sendEvent(argThat(event ->
                event.getStatus() == EventStatus.SUCCESS &&
                        event.getUserId().equals("1")
        ));
    }

    @Test
    void register_ShouldEncodePassword() {
        RegisterRequest registerRequest = new RegisterRequest(
                "test@example.com",
                "rawPassword",
                "John Doe",
                "Tech Corp",
                Set.of("Java")
        );

        String ipAddress = "127.0.0.1";
        String encodedPassword = "encodedPassword";

        when(ipAddressExtractor.getClientIpAddress(request)).thenReturn(ipAddress);
        when(userRepository.existsByEmail(any())).thenReturn(false);
        when(passwordEncoder.encode("rawPassword")).thenReturn("encodedPassword");

        UserModel savedUser = new UserModel();
        savedUser.setId(1L);
        savedUser.setEmail(registerRequest.email());
        savedUser.setPassword(encodedPassword);
        when(userRepository.save(any(UserModel.class))).thenReturn(savedUser);

        registrationService.register(registerRequest, request);

        verify(successfulRegistrationService).canRegister(ipAddress);
        verify(successfulRegistrationService).registerSuccessfulRegistration(ipAddress);
        verify(passwordEncoder).encode("rawPassword");
        verify(userRepository).save(argThat(user ->
                user.getPassword().equals("encodedPassword")));
        verify(registrationEventProducer).sendEvent(argThat(event ->
                event.getStatus() == EventStatus.SUCCESS &&
                        event.getUserId().equals("1")
        ));
    }
}
