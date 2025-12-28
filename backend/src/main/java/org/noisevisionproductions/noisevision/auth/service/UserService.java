package org.noisevisionproductions.noisevision.auth.service;

import lombok.RequiredArgsConstructor;
import org.noisevisionproductions.noisevision.auth.dto.UserInfoResponse;
import org.noisevisionproductions.noisevision.auth.model.UserModel;
import org.noisevisionproductions.noisevision.auth.repository.UserRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public UserInfoResponse getCurrentUserInfo(String email) {
        UserModel user = userRepository.findByEmailWIthProgrammingLanguages(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        return new UserInfoResponse(
                user.getEmail(),
                user.getRole().name(),
                user.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .collect(Collectors.toSet()),
                user.getName(),
                user.getCompanyName(),
                new HashSet<>(user.getProgrammingLanguages())
        );
    }
}
