package com.wilove.vaulten.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "vault_entries")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VaultEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Name is required")
    @Column(nullable = false)
    private String name;

    private String username;

    private String password; // Will be stored encrypted

    private String url;

    @Column(columnDefinition = "TEXT")
    private String notes; // Will be stored encrypted

    @NotNull(message = "Entry type is required")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private VaultEntryType type;

    private String category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @NotNull(message = "Owner user is required")
    private User user;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
