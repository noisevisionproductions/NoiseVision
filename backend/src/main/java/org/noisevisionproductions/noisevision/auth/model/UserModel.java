package org.noisevisionproductions.noisevision.auth.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.noisevisionproductions.noisevision.auth.model.enums.Role;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Setter
@Getter
@Entity
@Table(name = "users")
public class UserModel implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String email;

    private String password;
    private String name;
    private String companyName;

    @Enumerated(EnumType.STRING)
    private Role role = Role.USER;

    @ElementCollection
    @CollectionTable(
            name = "users_programming_languages",
            joinColumns = @JoinColumn(name = "user_id")
    )
    private Set<String> programmingLanguages = new HashSet<>();

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Set<SimpleGrantedAuthority> authorities = new HashSet<>();

        authorities.add(new SimpleGrantedAuthority("ROLE_" + role.name()));

        authorities.addAll(
                role.getAuthorities().stream()
                        .map(auth -> new SimpleGrantedAuthority(auth.name()))
                        .collect(Collectors.toSet())
        );

        return authorities;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public String getPassword() {
        return password;
    }
}
