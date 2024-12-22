package org.noisevisionproductions.portfolio.auth.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Collections;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(MockitoExtension.class)
class JwtServiceTest {

    private JwtService jwtService;
    private UserDetails userDetails;
    private static final String SECRET_KEY = "404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970";
    private static final long JWT_EXPIRATION = 86400000;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        ReflectionTestUtils.setField(jwtService, "secretKey", SECRET_KEY);
        ReflectionTestUtils.setField(jwtService, "jwtExpiration", JWT_EXPIRATION);

        userDetails = User.builder()
                .username("test@example.com")
                .password("password")
                .authorities(Collections.emptyList())
                .build();
    }

    @Test
    void generateToken_ShouldCreateValidToken() {
        String token = jwtService.generateToken(userDetails);

        assertThat(token).isNotNull();
        assertThat(jwtService.extractUsername(token)).isEqualTo(userDetails.getUsername());
        assertThat(jwtService.isTokenValid(token, userDetails)).isTrue();
    }

    @Test
    void generateToken_ShouldIncludeCorrectClaims() {
        String token = jwtService.generateToken(userDetails);
        Claims claims = extractClaims(token);

        assertThat(claims.getSubject()).isEqualTo(userDetails.getUsername());
        assertThat(claims.getIssuedAt())
                .isNotNull()
                .isCloseTo(new Date(), 1000);
        assertThat(claims.getExpiration())
                .isNotNull()
                .isCloseTo(new Date(System.currentTimeMillis() + JWT_EXPIRATION), 1000);
    }

    private Claims extractClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(Decoders.BASE64.decode(SECRET_KEY)))
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    @Test
    void extractUsername_ShouldThrownException_WhenTokenIsMalformed() {
        String malformedToken = "malformed.token.here";

        assertThatThrownBy(() -> jwtService.extractUsername(malformedToken))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Invalid JWT token");
    }

    @Test
    void isTokenValid_ShouldReturnFalse_WhenTokenIsExpired() throws InterruptedException {
        ReflectionTestUtils.setField(jwtService, "jwtExpiration", 100);
        String token = jwtService.generateToken(userDetails);

        Thread.sleep(200);

        assertThat(jwtService.isTokenValid(token, userDetails)).isFalse();
    }

    @Test
    void isTokenValid_ShouldReturnFalse_WhenUsernameMismatch() {
        String token = jwtService.generateToken(userDetails);
        UserDetails differentUser = User.builder()
                .username("different@example.com")
                .password("password")
                .authorities(Collections.emptyList())
                .build();

        assertThat(jwtService.isTokenValid(token, differentUser)).isFalse();
    }
}