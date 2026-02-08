package com.wilove.vaulten.data.remote.model

import kotlinx.serialization.Serializable

/**
 * Represents the type of entry stored in the vault.
 */
@Serializable
enum class VaultType {
    LOGIN, NOTE, CARD, IDENTITY
}

/**
 * Detailed information of a vault entry returned by the server.
 */
@Serializable
data class VaultEntryResponse(
    val id: Long,
    val name: String,
    val username: String? = null,
    val password: String? = null,
    val url: String? = null,
    val notes: String? = null,
    val type: VaultType,
    val category: String,
    val createdAt: String,
    val updatedAt: String
)

/**
 * Model used to create or update a vault entry.
 */
@Serializable
data class VaultEntryRequest(
    val name: String,
    val username: String? = null,
    val password: String? = null,
    val url: String? = null,
    val notes: String? = null,
    val type: VaultType,
    val category: String
)

/**
 * Synchronization response containing modified entries since a specific timestamp.
 */
@Serializable
data class SyncResponse(
    val updatedEntries: List<VaultEntryResponse>,
    val serverTime: String
)
