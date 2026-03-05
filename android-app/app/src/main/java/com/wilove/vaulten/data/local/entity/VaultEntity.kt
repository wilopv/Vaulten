package com.wilove.vaulten.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "vault_entries")
data class VaultEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val username: String,
    val password: String,
    val url: String?,
    val lastModified: Long,
    val synced: Boolean = true
)
