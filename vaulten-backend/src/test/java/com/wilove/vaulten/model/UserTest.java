package com.wilove.vaulten.model;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for User entity validation
 */
class UserTest {

        private Validator validator;

        @BeforeEach
        void setUp() {
                ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
                validator = factory.getValidator();
        }

        @Test
        void testValidUser() {
                // Given
                User user = User.builder()
                                .username("testuser")
                                .email("test@example.com")
                                .password("$2a$10$abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ") // BCrypt hash
                                .role(Role.USER)
                                .build();

                // When
                Set<ConstraintViolation<User>> violations = validator.validate(user);

                // Then
                assertTrue(violations.isEmpty(), "Valid user should have no validation errors");
        }

        @Test
        void testUsernameIsBlank() {
                // Given
                User user = User.builder()
                                .username("")
                                .email("test@example.com")
                                .password("$2a$10$abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ")
                                .role(Role.USER)
                                .build();

                // When
                Set<ConstraintViolation<User>> violations = validator.validate(user);

                // Then
                assertFalse(violations.isEmpty());
                assertTrue(violations.stream()
                                .anyMatch(v -> v.getMessage().contains("Username is required")));
        }

        @Test
        void testUsernameTooShort() {
                // Given
                User user = User.builder()
                                .username("ab") // Too short
                                .email("test@example.com")
                                .password("$2a$10$abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ")
                                .role(Role.USER)
                                .build();

                // When
                Set<ConstraintViolation<User>> violations = validator.validate(user);

                // Then
                assertFalse(violations.isEmpty());
                assertTrue(violations.stream()
                                .anyMatch(v -> v.getMessage().contains("between 3 and 50 characters")));
        }

        @Test
        void testUsernameTooLong() {
                // Given
                String longUsername = "a".repeat(51);
                User user = User.builder()
                                .username(longUsername)
                                .email("test@example.com")
                                .password("$2a$10$abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ")
                                .role(Role.USER)
                                .build();

                // When
                Set<ConstraintViolation<User>> violations = validator.validate(user);

                // Then
                assertFalse(violations.isEmpty());
                assertTrue(violations.stream()
                                .anyMatch(v -> v.getMessage().contains("between 3 and 50 characters")));
        }

        @Test
        void testEmailIsBlank() {
                // Given
                User user = User.builder()
                                .username("testuser")
                                .email("")
                                .password("$2a$10$abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ")
                                .role(Role.USER)
                                .build();

                // When
                Set<ConstraintViolation<User>> violations = validator.validate(user);

                // Then
                assertFalse(violations.isEmpty());
                assertTrue(violations.stream()
                                .anyMatch(v -> v.getMessage().contains("Email is required")));
        }

        @Test
        void testEmailIsInvalid() {
                // Given
                User user = User.builder()
                                .username("testuser")
                                .email("invalid-email")
                                .password("$2a$10$abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ")
                                .role(Role.USER)
                                .build();

                // When
                Set<ConstraintViolation<User>> violations = validator.validate(user);

                // Then
                assertFalse(violations.isEmpty());
                assertTrue(violations.stream()
                                .anyMatch(v -> v.getMessage().contains("Email must be valid")));
        }

        @Test
        void testPasswordIsBlank() {
                // Given
                User user = User.builder()
                                .username("testuser")
                                .email("test@example.com")
                                .password("")
                                .role(Role.USER)
                                .build();

                // When
                Set<ConstraintViolation<User>> violations = validator.validate(user);

                // Then
                assertFalse(violations.isEmpty());
                assertTrue(violations.stream()
                                .anyMatch(v -> v.getMessage().contains("Password is required") ||
                                                v.getMessage().contains("at least 8 characters")));
        }

        @Test
        void testRoleDefaultsToUser() {
                // Given
                User user = User.builder()
                                .username("testuser")
                                .email("test@example.com")
                                .password("$2a$10$abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ")
                                .role(Role.USER)
                                .build();

                // Then
                assertEquals(Role.USER, user.getRole());
        }

        @Test
        void testAdminRole() {
                // Given
                User user = User.builder()
                                .username("admin")
                                .email("admin@example.com")
                                .password("$2a$10$abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ")
                                .role(Role.ADMIN)
                                .build();

                // Then
                assertEquals(Role.ADMIN, user.getRole());
        }
}
