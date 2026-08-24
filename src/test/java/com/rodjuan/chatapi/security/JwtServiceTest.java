package com.rodjuan.chatapi.security;

import io.jsonwebtoken.JwtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    private JwtService jwtService;
    private UserDetails userDetails;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        ReflectionTestUtils.setField(jwtService, "secretKey", "c2VjcmV0b2ZpY2lhbGNoYXRhcGljbGF2ZXNlY3JldGE=");
        ReflectionTestUtils.setField(jwtService, "expirationMs", 3_600_000L);
        userDetails = User.withUsername("juan@gmail.com").password("ignored").roles("USER").build();
    }

    @Test
    void generatesAndValidatesToken() {
        String token = jwtService.generateToken(userDetails);

        assertEquals("juan@gmail.com", jwtService.extractEmail(token));
        assertTrue(jwtService.isTokenValid(token, userDetails));
    }

    @Test
    void rejectsTokenForAnotherUser() {
        String token = jwtService.generateToken(userDetails);
        UserDetails anotherUser = User.withUsername("other@example.com")
                .password("ignored")
                .roles("USER")
                .build();

        assertFalse(jwtService.isTokenValid(token, anotherUser));
    }

    @Test
    void rejectsExpiredToken() {
        ReflectionTestUtils.setField(jwtService, "expirationMs", -1L);
        String token = jwtService.generateToken(userDetails);

        assertFalse(jwtService.isTokenValid(token, userDetails));
    }

    @Test
    void rejectsMalformedToken() {
        assertThrows(JwtException.class, () -> jwtService.extractEmail("not-a-jwt"));
    }
}
