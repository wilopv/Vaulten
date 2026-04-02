package com.wilove.vaulten.domain.usecase

import com.wilove.vaulten.data.export.ExportEntry
import com.wilove.vaulten.data.export.VaultExportImportManager
import com.wilove.vaulten.domain.model.Credential
import com.wilove.vaulten.domain.repository.VaultRepository
import kotlinx.coroutines.flow.first

class ImportVaultUseCase(private val repository: VaultRepository) {

    data class ImportResult(val imported: Int, val skipped: Int)

    suspend fun fromCsv(csvContent: String): ImportResult {
        val entries = VaultExportImportManager.parseCsv(csvContent)
        return mergeEntries(entries)
    }

    /**
     * @throws IllegalArgumentException if the password is wrong or the file is corrupt.
     */
    suspend fun fromEncryptedJson(fileContent: String, password: String): ImportResult {
        val entries = VaultExportImportManager.decryptFromJson(fileContent, password)
        return mergeEntries(entries)
    }

    private suspend fun mergeEntries(entries: List<ExportEntry>): ImportResult {
        if (entries.isEmpty()) return ImportResult(0, 0)

        val existing = repository.getAllCredentials().first()
        val existingKeys = existing
            .filter { it.deletedAt == null }
            .map { dedupeKey(it.url, it.username) }
            .toSet()

        var imported = 0
        var skipped = 0

        for (entry in entries) {
            val key = dedupeKey(entry.url, entry.username)
            if (key in existingKeys) {
                skipped++
                continue
            }
            try {
                repository.saveCredential(
                    Credential(
                        id = "0",
                        name = entry.name,
                        username = entry.username,
                        password = entry.password,
                        url = entry.url,
                        androidPackageName = entry.androidPackageName
                    )
                )
                imported++
            } catch (_: Exception) {
                skipped++
            }
        }

        return ImportResult(imported, skipped)
    }

    private fun dedupeKey(url: String?, username: String): String {
        val normalizedUrl = url?.trimEnd('/')?.lowercase() ?: ""
        return "$normalizedUrl|${username.lowercase()}"
    }
}
