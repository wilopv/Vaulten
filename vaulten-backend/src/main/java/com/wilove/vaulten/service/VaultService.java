package com.wilove.vaulten.service;

import com.wilove.vaulten.exception.AccessDeniedException;
import com.wilove.vaulten.model.User;
import com.wilove.vaulten.model.VaultEntry;
import com.wilove.vaulten.repository.VaultEntryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VaultService {

    private final VaultEntryRepository vaultEntryRepository;
    private final EncryptionService encryptionService;

    @Transactional
    public VaultEntry createEntry(VaultEntry entry, User user) {
        entry.setUser(user);
        encryptSensitiveFields(entry);
        return vaultEntryRepository.save(entry);
    }

    public List<VaultEntry> getEntriesForUser(User user) {
        return vaultEntryRepository.findAllByUser(user).stream()
                .peek(this::decryptSensitiveFields)
                .collect(Collectors.toList());
    }

    public List<VaultEntry> getEntriesModifiedSince(User user, LocalDateTime since) {
        return vaultEntryRepository.findByUserAndUpdatedAtAfter(user, since).stream()
                .peek(this::decryptSensitiveFields)
                .collect(Collectors.toList());
    }

    public VaultEntry getEntryById(Long id, User user) {
        VaultEntry entry = vaultEntryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Entry not found"));

        if (!entry.getUser().getId().equals(user.getId())) {
            throw new AccessDeniedException();
        }

        decryptSensitiveFields(entry);
        return entry;
    }

    @Transactional
    public VaultEntry updateEntry(Long id, VaultEntry updatedEntry, User user) {
        VaultEntry existingEntry = getEntryById(id, user);

        existingEntry.setName(updatedEntry.getName());
        existingEntry.setUsername(updatedEntry.getUsername());
        existingEntry.setPassword(updatedEntry.getPassword());
        existingEntry.setUrl(updatedEntry.getUrl());
        existingEntry.setNotes(updatedEntry.getNotes());
        existingEntry.setType(updatedEntry.getType());
        existingEntry.setCategory(updatedEntry.getCategory());

        encryptSensitiveFields(existingEntry);
        return vaultEntryRepository.save(existingEntry);
    }

    @Transactional
    public void deleteEntry(Long id, User user) {
        VaultEntry entry = getEntryById(id, user);
        vaultEntryRepository.delete(entry);
    }

    private void encryptSensitiveFields(VaultEntry entry) {
        if (entry.getPassword() != null) {
            entry.setPassword(encryptionService.encrypt(entry.getPassword()));
        }
        if (entry.getNotes() != null) {
            entry.setNotes(encryptionService.encrypt(entry.getNotes()));
        }
    }

    private void decryptSensitiveFields(VaultEntry entry) {
        if (entry.getPassword() != null) {
            entry.setPassword(encryptionService.decrypt(entry.getPassword()));
        }
        if (entry.getNotes() != null) {
            entry.setNotes(encryptionService.decrypt(entry.getNotes()));
        }
    }
}
