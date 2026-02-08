package com.wilove.vaulten.dto;

import com.wilove.vaulten.model.VaultEntry;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class SyncResponse {
    private List<VaultEntry> updatedEntries;
    private LocalDateTime serverTime;
}
