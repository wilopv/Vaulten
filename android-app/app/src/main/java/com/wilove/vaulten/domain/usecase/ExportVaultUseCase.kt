package com.wilove.vaulten.domain.usecase

import com.wilove.vaulten.data.export.VaultExportImportManager
import com.wilove.vaulten.domain.repository.VaultRepository
import kotlinx.coroutines.flow.first

class ExportVaultUseCase(private val repository: VaultRepository) {

    suspend fun toCsv(): String {
        val credentials = repository.getAllCredentials().first()
            .filter { it.deletedAt == null }
        return VaultExportImportManager.toCsv(credentials)
    }

    suspend fun toEncryptedJson(password: String): String {
        val credentials = repository.getAllCredentials().first()
            .filter { it.deletedAt == null }
        return VaultExportImportManager.encryptToJson(credentials, password)
    }
}
