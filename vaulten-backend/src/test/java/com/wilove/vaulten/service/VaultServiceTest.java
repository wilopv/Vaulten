package com.wilove.vaulten.service;

import com.wilove.vaulten.exception.AccessDeniedException;
import com.wilove.vaulten.model.User;
import com.wilove.vaulten.model.VaultEntry;
import com.wilove.vaulten.model.VaultEntryType;
import com.wilove.vaulten.repository.VaultEntryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VaultServiceTest {

    @Mock
    private VaultEntryRepository vaultEntryRepository;

    @Mock
    private EncryptionService encryptionService;

    @InjectMocks
    private VaultService vaultService;

    private User testUser;
    private VaultEntry testEntry;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");

        testEntry = VaultEntry.builder()
                .id(1L)
                .name("My Bank")
                .username("bankuser")
                .password("plain_password")
                .notes("secret notes")
                .type(VaultEntryType.LOGIN)
                .user(testUser)
                .build();
    }

    @Test
    void createEntry_ShouldEncryptSensitiveFields() {
        // Given
        when(encryptionService.encrypt("plain_password")).thenReturn("encrypted_password");
        when(encryptionService.encrypt("secret notes")).thenReturn("encrypted_notes");
        when(vaultEntryRepository.save(any(VaultEntry.class))).thenReturn(testEntry);

        // When
        VaultEntry result = vaultService.createEntry(testEntry, testUser);

        // Then
        verify(encryptionService).encrypt("plain_password");
        verify(encryptionService).encrypt("secret notes");
        verify(vaultEntryRepository).save(testEntry);
        assertEquals(testUser, result.getUser());
    }

    @Test
    void getEntriesForUser_ShouldDecryptSensitiveFields() {
        // Given
        testEntry.setPassword("encrypted_password");
        testEntry.setNotes("encrypted_notes");
        when(vaultEntryRepository.findAllByUser(testUser)).thenReturn(List.of(testEntry));
        when(encryptionService.decrypt("encrypted_password")).thenReturn("plain_password");
        when(encryptionService.decrypt("encrypted_notes")).thenReturn("secret notes");

        // When
        List<VaultEntry> entries = vaultService.getEntriesForUser(testUser);

        // Then
        assertEquals(1, entries.size());
        assertEquals("plain_password", entries.get(0).getPassword());
        assertEquals("secret notes", entries.get(0).getNotes());
    }

    @Test
    void getEntryById_ShouldReturnDecryptedEntry_WhenUserIsOwner() {
        // Given
        testEntry.setPassword("encrypted_password");
        when(vaultEntryRepository.findById(1L)).thenReturn(Optional.of(testEntry));
        when(encryptionService.decrypt("encrypted_password")).thenReturn("plain_password");

        // When
        VaultEntry result = vaultService.getEntryById(1L, testUser);

        // Then
        assertNotNull(result);
        assertEquals("plain_password", result.getPassword());
    }

    @Test
    void getEntryById_ShouldThrowException_WhenUserIsNotOwner() {
        // Given
        User otherUser = new User();
        otherUser.setId(2L);
        when(vaultEntryRepository.findById(1L)).thenReturn(Optional.of(testEntry));

        // When & Then
        assertThrows(AccessDeniedException.class, () -> vaultService.getEntryById(1L, otherUser));
    }

    @Test
    void updateEntry_ShouldEncryptAndSave_WhenUserIsOwner() {
        // Given
        VaultEntry updatedData = VaultEntry.builder()
                .name("Updated Name")
                .password("new_plain_pass")
                .build();

        when(vaultEntryRepository.findById(1L)).thenReturn(Optional.of(testEntry));
        when(encryptionService.encrypt("new_plain_pass")).thenReturn("new_encrypted_pass");
        when(vaultEntryRepository.save(any(VaultEntry.class))).thenReturn(testEntry);

        // When
        VaultEntry result = vaultService.updateEntry(1L, updatedData, testUser);

        // Then
        assertEquals("Updated Name", result.getName());
        verify(encryptionService).encrypt("new_plain_pass");
        verify(vaultEntryRepository).save(any(VaultEntry.class));
    }

    @Test
    void deleteEntry_ShouldCallRepository_WhenUserIsOwner() {
        // Given
        when(vaultEntryRepository.findById(1L)).thenReturn(Optional.of(testEntry));

        // When
        vaultService.deleteEntry(1L, testUser);

        // Then
        verify(vaultEntryRepository).delete(testEntry);
    }
}
