package com.wilove.vaulten.repository;

import com.wilove.vaulten.model.Role;
import com.wilove.vaulten.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for UserRepository with H2 database
 */
@DataJpaTest
@ActiveProfiles("test")
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void testSaveUser() {
        // Given
        User user = User.builder()
                .username("testuser")
                .email("test@example.com")
                .password("$2a$10$hashedpassword1234567890123456789012345678901234")
                .role(Role.USER)
                .build();

        // When
        User savedUser = userRepository.save(user);

        // Then
        assertNotNull(savedUser.getId());
        assertEquals("testuser", savedUser.getUsername());
        assertEquals("test@example.com", savedUser.getEmail());
        assertEquals(Role.USER, savedUser.getRole());
        assertNotNull(savedUser.getCreatedAt());
        assertNotNull(savedUser.getUpdatedAt());
    }

    @Test
    void testFindByUsername() {
        // Given
        User user = User.builder()
                .username("testuser")
                .email("test@example.com")
                .password("$2a$10$hashedpassword1234567890123456789012345678901234")
                .role(Role.USER)
                .build();
        userRepository.save(user);

        // When
        Optional<User> found = userRepository.findByUsername("testuser");

        // Then
        assertTrue(found.isPresent());
        assertEquals("testuser", found.get().getUsername());
    }

    @Test
    void testFindByUsernameNotFound() {
        // When
        Optional<User> found = userRepository.findByUsername("nonexistent");

        // Then
        assertFalse(found.isPresent());
    }

    @Test
    void testFindByEmail() {
        // Given
        User user = User.builder()
                .username("testuser")
                .email("test@example.com")
                .password("$2a$10$hashedpassword1234567890123456789012345678901234")
                .role(Role.USER)
                .build();
        userRepository.save(user);

        // When
        Optional<User> found = userRepository.findByEmail("test@example.com");

        // Then
        assertTrue(found.isPresent());
        assertEquals("test@example.com", found.get().getEmail());
    }

    @Test
    void testFindByEmailNotFound() {
        // When
        Optional<User> found = userRepository.findByEmail("nonexistent@example.com");

        // Then
        assertFalse(found.isPresent());
    }

    @Test
    void testExistsByUsername() {
        // Given
        User user = User.builder()
                .username("testuser")
                .email("test@example.com")
                .password("$2a$10$hashedpassword1234567890123456789012345678901234")
                .role(Role.USER)
                .build();
        userRepository.save(user);

        // When & Then
        assertTrue(userRepository.existsByUsername("testuser"));
        assertFalse(userRepository.existsByUsername("nonexistent"));
    }

    @Test
    void testExistsByEmail() {
        // Given
        User user = User.builder()
                .username("testuser")
                .email("test@example.com")
                .password("$2a$10$hashedpassword1234567890123456789012345678901234")
                .role(Role.USER)
                .build();
        userRepository.save(user);

        // When & Then
        assertTrue(userRepository.existsByEmail("test@example.com"));
        assertFalse(userRepository.existsByEmail("nonexistent@example.com"));
    }

    @Test
    void testUsernameUniqueConstraint() {
        // Given
        User user1 = User.builder()
                .username("testuser")
                .email("test1@example.com")
                .password("$2a$10$hashedpassword1234567890123456789012345678901234")
                .role(Role.USER)
                .build();
        userRepository.save(user1);

        User user2 = User.builder()
                .username("testuser") // Duplicate username
                .email("test2@example.com")
                .password("$2a$10$hashedpassword1234567890123456789012345678901234")
                .role(Role.USER)
                .build();

        // When & Then
        assertThrows(Exception.class, () -> {
            userRepository.save(user2);
            userRepository.flush();
        });
    }

    @Test
    void testEmailUniqueConstraint() {
        // Given
        User user1 = User.builder()
                .username("testuser1")
                .email("test@example.com")
                .password("$2a$10$hashedpassword1234567890123456789012345678901234")
                .role(Role.USER)
                .build();
        userRepository.save(user1);

        User user2 = User.builder()
                .username("testuser2")
                .email("test@example.com") // Duplicate email
                .password("$2a$10$hashedpassword1234567890123456789012345678901234")
                .role(Role.USER)
                .build();

        // When & Then
        assertThrows(Exception.class, () -> {
            userRepository.save(user2);
            userRepository.flush();
        });
    }
}
