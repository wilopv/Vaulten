package com.wilove.vaulten.data.remote.mapper

import com.wilove.vaulten.data.remote.model.VaultEntryResponse
import com.wilove.vaulten.data.remote.model.VaultType
import com.wilove.vaulten.domain.model.Credential
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

/**
 * Mapper functions to convert between API models and Domain models.
 */

fun VaultEntryResponse.toDomain(): Credential {
    return Credential(
        id = this.id.toString(),
        name = this.name,
        username = this.username ?: "",
        password = this.password ?: "",
        url = this.url,
        lastModified = parseTimestamp(this.updatedAt)
    )
}

fun List<VaultEntryResponse>.toDomain(): List<Credential> {
    return this.map { it.toDomain() }
}

private fun parseTimestamp(timestamp: String): Long {
    return try {
        val odt = OffsetDateTime.parse(timestamp)
        odt.toInstant().toEpochMilli()
    } catch (e: Exception) {
        System.currentTimeMillis()
    }
}
