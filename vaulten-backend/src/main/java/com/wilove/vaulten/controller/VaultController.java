package com.wilove.vaulten.controller;

import com.wilove.vaulten.dto.SyncResponse;
import com.wilove.vaulten.model.User;
import com.wilove.vaulten.model.VaultEntry;
import com.wilove.vaulten.service.VaultService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/vault")
@RequiredArgsConstructor
@Tag(name = "Vault", description = "Vault entry management endpoints")
@SecurityRequirement(name = "bearerAuth")
public class VaultController {

    private final VaultService vaultService;

    @GetMapping
    @Operation(summary = "Get all vault entries for the current user")
    public ResponseEntity<List<VaultEntry>> getAllEntries() {
        return ResponseEntity.ok(vaultService.getEntriesForUser(getCurrentUser()));
    }

    @GetMapping("/sync")
    @Operation(summary = "Get modified entries since last sync")
    public ResponseEntity<SyncResponse> sync(
            @Parameter(description = "Last sync timestamp (ISO 8601)") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime since) {

        LocalDateTime serverTime = LocalDateTime.now();
        List<VaultEntry> entries = (since == null)
                ? vaultService.getEntriesForUser(getCurrentUser())
                : vaultService.getEntriesModifiedSince(getCurrentUser(), since);

        return ResponseEntity.ok(SyncResponse.builder()
                .updatedEntries(entries)
                .serverTime(serverTime)
                .build());
    }

    @PostMapping
    @Operation(summary = "Create a new vault entry")
    public ResponseEntity<VaultEntry> createEntry(@Valid @RequestBody VaultEntry entry) {
        return ResponseEntity.ok(vaultService.createEntry(entry, getCurrentUser()));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a specific vault entry by ID")
    public ResponseEntity<VaultEntry> getEntryById(@PathVariable Long id) {
        return ResponseEntity.ok(vaultService.getEntryById(id, getCurrentUser()));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing vault entry")
    public ResponseEntity<VaultEntry> updateEntry(@PathVariable Long id, @Valid @RequestBody VaultEntry entry) {
        return ResponseEntity.ok(vaultService.updateEntry(id, entry, getCurrentUser()));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a vault entry")
    public ResponseEntity<Void> deleteEntry(@PathVariable Long id) {
        vaultService.deleteEntry(id, getCurrentUser());
        return ResponseEntity.noContent().build();
    }

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof User) {
            return (User) authentication.getPrincipal();
        }
        // This case is for tests where SecurityContext might be empty or mock
        // In real execution, the JWT filter ensures a User is present for protected
        // paths
        return null;
    }
}
