package org.noisevisionproductions.portfolio.auth.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.noisevisionproductions.portfolio.auth.dto.UserInfoResponse;
import org.noisevisionproductions.portfolio.auth.model.UserModel;
import org.noisevisionproductions.portfolio.auth.model.enums.Role;
import org.noisevisionproductions.portfolio.auth.repository.UserRepository;

import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Test
    void getCurrentUserInfo_ShouldReturnUserInfo_WhenUserExists() {
        String email = "test@example.com";
        UserModel user = new UserModel();
        user.setEmail(email);
        user.setName("John Doe");
        user.setCompanyName("Tech Corp");
        user.setRole(Role.USER);
        user.setProgrammingLanguages(Set.of("Java", "Python"));

        when(userRepository.findByEmailWIthProgrammingLanguages(email))
                .thenReturn(Optional.of(user));

        UserInfoResponse response = userService.getCurrentUserInfo(email);

        assertThat(response).isNotNull();
        assertThat(response.email()).isEqualTo(email);
        assertThat(response.name()).isEqualTo("John Doe");
        assertThat(response.companyName()).isEqualTo("Tech Corp");
        assertThat(response.role()).isEqualTo(Role.USER.name());
        assertThat(response.authorities()).contains("ROLE_USER");
        assertThat(response.programmingLanguages()).containsExactlyInAnyOrder("Java", "Python");

        verify(userRepository).findByEmailWIthProgrammingLanguages(email);
    }
}