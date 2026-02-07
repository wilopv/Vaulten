package com.wilove.vaulten.service;

import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for JwtService
 */
class JwtServiceTest {

    private JwtService jwtService;
    private final String testUsername = "testuser";
    private final String testSecret = "dGVzdC1zZWNyZXQta2V5LWZvci10ZXN0aW5nLW1pbmltdW0tMjU2LWJpdHMtcmVxdWlyZWQ="; // Base64
                                                                                                                  // encoded
    private final Long testExpiration = 3600000L; // 1 hour

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        // Set private fields using reflection
        ReflectionTestUtils.setField(jwtService, "secret", testSecret);
        ReflectionTestUtils.setField(jwtService, "expiration", testExpiration);
    }

    @Test
    void testGenerateToken() {
        // When
        String token = jwtService.generateToken(testUsername);

        // Then
        assertNotNull(token);
        assertFalse(token.isEmpty());
        assertTrue(token.split("\\.").length == 3, "JWT should have 3 parts separated by dots");
    }

    @Test
    void testGenerateTokenWithExtraClaims() {
        // Given
        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("role", "USER");
        extraClaims.put("email", "test@example.com");

        // When
        String token = jwtService.generateToken(extraClaims, testUsername);

        // Then
        assertNotNull(token);
        assertFalse(token.isEmpty());
    }

    @Test
    void testExtractUsername() {
        // Given
        String token = jwtService.generateToken(testUsername);

        // When
        String extractedUsername = jwtService.extractUsername(token);

        // Then
        assertEquals(testUsername, extractedUsername);
    }

    @Test
    void testExtractExpiration() {
        // Given
        String token = jwtService.generateToken(testUsername);
        Date now = new Date();

        // When
        Date expiration = jwtService.extractExpiration(token);

        // Then
        assertNotNull(expiration);
        assertTrue(expiration.after(now), "Expiration should be in the future");
    }

    @Test
    void testExtractClaim() {
        // Given
        String token = jwtService.generateToken(testUsername);

        // When
        String subject = jwtService.extractClaim(token, Claims::getSubject);

        // Then
        assertEquals(testUsername, subject);
    }

    @Test
    void testValidateTokenSuccess() {
        // Given
        String token = jwtService.generateToken(testUsername);

        // When
        Boolean isValid = jwtService.validateToken(token, testUsername);

        // Then
        assertTrue(isValid);
    }

    @Test
    void testValidateTokenWrongUsername() {
        // Given
        String token = jwtService.generateToken(testUsername);

        // When
        Boolean isValid = jwtService.validateToken(token, "wronguser");

        // Then
        assertFalse(isValid);
    }

    @Test
    void testValidateTokenExpired() {
        // Given - Create service with very short expiration
        JwtService shortExpirationService = new JwtService();
        ReflectionTestUtils.setField(shortExpirationService, "secret", testSecret);
        ReflectionTestUtils.setField(shortExpirationService, "expiration", 1L); // 1ms = very short

        String token = shortExpirationService.generateToken(testUsername);

        // Sleep to ensure token expires
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // When/Then - Should return false for expired token
        Boolean isValid = shortExpirationService.validateToken(token, testUsername);
        assertFalse(isValid, "Expired token should not be valid");
    }

    @Test
    void testTokenStructure() {
        // Given
        String token = jwtService.generateToken(testUsername);

        // Then
        String[] parts = token.split("\\.");
        assertEquals(3, parts.length, "JWT should have header, payload, and signature");

        // Each part should be base64 encoded (not empty)
        assertTrue(parts[0].length() > 0, "Header should not be empty");
        assertTrue(parts[1].length() > 0, "Payload should not be empty");
        assertTrue(parts[2].length() > 0, "Signature should not be empty");
    }

    @Test
    void testTokensAreDifferent() {
        // When
        String token1 = jwtService.generateToken(testUsername);

        // Sleep to ensure different timestamp (1 second)
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        String token2 = jwtService.generateToken(testUsername);

        // Then
        assertNotEquals(token1, token2, "Tokens generated at different times should be different");
    }
}
