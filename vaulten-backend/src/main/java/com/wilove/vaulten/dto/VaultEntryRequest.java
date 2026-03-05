package com.wilove.vaulten.dto;

import com.wilove.vaulten.model.VaultEntryType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for creating or updating a vault entry
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VaultEntryRequest {

    @NotBlank(message = "Name is required")
    private String name;

    private String username;

    private String password;

    private String url;

    private String notes;

    @NotNull(message = "Entry type is required")
    private VaultEntryType type;

    private String category;
}
