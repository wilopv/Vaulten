package com.wilove.vaulten.repository;

import com.wilove.vaulten.model.Role;
import com.wilove.vaulten.model.User;
import com.wilove.vaulten.model.VaultEntry;
import com.wilove.vaulten.model.VaultEntryType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class VaultEntryRepositoryTest {

    @Autowired
    private VaultEntryRepository vaultEntryRepository;

    @Autowired
    private UserRepository userRepository;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .username("testuser")
                .email("test@example.com")
                .password("password123")
                .role(Role.USER)
                .build();
        userRepository.save(testUser);
    }

    @Test
    void testSaveAndFindVaultEntry() {
        VaultEntry entry = VaultEntry.builder()
                .name("Test Login")
                .username("user123")
                .password("encrypted_pass")
                .type(VaultEntryType.LOGIN)
                .user(testUser)
                .build();

        VaultEntry savedEntry = vaultEntryRepository.save(entry);

        assertNotNull(savedEntry.getId());
        assertEquals("Test Login", savedEntry.getName());
        assertNotNull(savedEntry.getCreatedAt());
        assertNotNull(savedEntry.getUpdatedAt());
    }

    @Test
    void testFindAllByUser() {
        VaultEntry entry1 = VaultEntry.builder()
                .name("Login 1")
                .type(VaultEntryType.LOGIN)
                .user(testUser)
                .build();

        VaultEntry entry2 = VaultEntry.builder()
                .name("Note 1")
                .type(VaultEntryType.NOTE)
                .user(testUser)
                .build();

        vaultEntryRepository.save(entry1);
        vaultEntryRepository.save(entry2);

        List<VaultEntry> userEntries = vaultEntryRepository.findAllByUser(testUser);

        assertEquals(2, userEntries.size());
    }

    @Test
    void testFindByUserAndNameContainingIgnoreCase() {
        VaultEntry entry = VaultEntry.builder()
                .name("Gmail Account")
                .type(VaultEntryType.LOGIN)
                .user(testUser)
                .build();
        vaultEntryRepository.save(entry);

        List<VaultEntry> results = vaultEntryRepository.findByUserAndNameContainingIgnoreCase(testUser, "GMAIL");

        assertFalse(results.isEmpty());
        assertEquals("Gmail Account", results.get(0).getName());
    }
}
