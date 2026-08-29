package org.noisevisionproductions.noisevision.security;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.noisevisionproductions.noisevision.auth.component.CustomAuthenticationProvider;
import org.noisevisionproductions.noisevision.auth.security.JwtAuthFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;
    private final CustomAuthenticationProvider customAuthenticationProvider;

    private static final String[] SWAGGER_PATHS = {
            "/swagger-ui.html",
            "/swagger-ui/**",
            "/api-docs/**",
            "/api-docs",
            "/v3/api-docs/**",
            "/v3/api-docs"
    };

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                .csrf(AbstractHttpConfigurer::disable)
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint((request, response, authException) -> {
                            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
                        })
                )
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers(SWAGGER_PATHS).permitAll()
                        .requestMatchers("/api/auth/register", "/api/auth/login").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/projects/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/files/**").permitAll()
                        .requestMatchers("/api/errors/**").permitAll()
                        .requestMatchers("/api/auth/me").authenticated()
                        .requestMatchers("/api/kafka/**", "/api/geoip/**").hasAuthority("ACCESS_KAFKA_DASHBOARD")
                        .requestMatchers(HttpMethod.POST, "/api/projects/**").hasAuthority("CREATE_PROJECTS")
                        .requestMatchers(HttpMethod.PUT, "/api/projects/**").hasAuthority("EDIT_PROJECTS")
                        .requestMatchers(HttpMethod.DELETE, "/api/projects/**").hasAuthority("DELETE_PROJECTS")
                        .anyRequest().authenticated()
                )
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authenticationProvider(customAuthenticationProvider)
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // Dodaj wszystkie możliwe adresy produkcyjne i deweloperskie
        configuration.setAllowedOriginPatterns(Arrays.asList(
                "http://localhost:3000",          // development
                "http://localhost",               // local docker
                "https://noisevision.app",        // przykładowa domena produkcyjna
                "https://*.noisevision.app",      // subdomeny
                "https://*.app.goog"              // domeny App Engine
        ));

        // Reszta konfiguracji pozostaje bez zmian
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "HEAD"));
        configuration.setAllowedHeaders(Arrays.asList(
                "Authorization",
                "Content-Type",
                "X-Forwarded-For",
                "X-Forwarded-Host",
                "X-Forwarded-Proto",
                "X-Real-IP",
                "Access-Control-Allow-Origin",
                "Access-Control-Allow-Methods",
                "Access-Control-Allow-Headers",
                "Access-Control-Max-Age",
                "Access-Control-Request-Headers",
                "Access-Control-Request-Method"
        ));
        configuration.setExposedHeaders(List.of("Authorization"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        // Lepiej użyć zmiennych środowiskowych dla adresów
        String prodDomain = System.getenv("ALLOWED_ORIGIN");
        if (prodDomain != null && !prodDomain.isEmpty()) {
            configuration.addAllowedOrigin(prodDomain);
        }

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}