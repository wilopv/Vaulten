package com.wilove.vaulten.data.repository

import com.wilove.vaulten.data.local.dao.VaultDao
import com.wilove.vaulten.data.local.mapper.toDomain
import com.wilove.vaulten.data.local.mapper.toDomainList
import com.wilove.vaulten.data.local.mapper.toEntity
import com.wilove.vaulten.data.remote.VaultApiService
import com.wilove.vaulten.data.remote.mapper.toDomain
import com.wilove.vaulten.data.remote.model.VaultEntryRequest
import com.wilove.vaulten.data.remote.model.VaultType
import com.wilove.vaulten.domain.model.Credential
import com.wilove.vaulten.domain.model.SecurityAlert
import com.wilove.vaulten.domain.repository.VaultRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import retrofit2.Response

/**
 * Repository responsible for managing vault entries and synchronization.
 */
class VaultRepositoryImpl(
    private val apiService: VaultApiService,
    private val vaultDao: VaultDao
) : VaultRepository {

    override fun getRecentCredentials(limit: Int): Flow<List<Credential>> {
        return vaultDao.getRecentCredentials(limit).map { it.toDomainList() }
    }

    override fun getAllCredentials(): Flow<List<Credential>> {
        return vaultDao.getAllCredentials().map { it.toDomainList() }
    }

    override suspend fun getSecurityAlerts(): List<SecurityAlert> {
        return emptyList() // TODO: Implement if needed
    }

    override suspend fun getCredentialById(id: String): Credential? {
        // Prefer local for details, then fetch remote? For now, local is cache.
        return vaultDao.getCredentialById(id)?.toDomain()
    }

    override suspend fun saveCredential(credential: Credential) {
        val idLong = credential.id.toLongOrNull()
        val request = VaultEntryRequest(
            name = credential.name,
            username = credential.username,
            password = credential.password,
            url = credential.url,
            androidPackageName = credential.androidPackageName,
            type = VaultType.LOGIN,
            category = "General"
        )

        val result = if (idLong == null || idLong == 0L) {
            handleApiCall { apiService.createEntry(request) }
        } else {
            handleApiCall { apiService.updateEntry(idLong, request) }
        }
        
        if (result.isSuccess) {
            val remoteEntry = result.getOrNull()
            if (remoteEntry != null) {
                // Use the server-assigned ID but keep the original plaintext password.
                // Never trust the API response's password field — the backend may return
                // an encrypted or transformed value depending on its implementation.
                val serverAssignedId = remoteEntry.id.toString()
                vaultDao.insertCredential(credential.copy(id = serverAssignedId).toEntity())
            }
        } else {
            throw result.exceptionOrNull() ?: Exception("Failed to save credential")
        }
    }

    override suspend fun deleteCredential(id: String) {
        // Soft delete only — the entry stays on the server until permanently deleted.
        // This allows restoring without any server interaction.
        vaultDao.softDeleteCredential(id, System.currentTimeMillis())
    }

    override fun getDeletedCredentials(): Flow<List<Credential>> {
        return vaultDao.getDeletedCredentials().map { it.toDomainList() }
    }

    override suspend fun restoreCredential(id: String) {
        // Restore is local only — the server entry was already deleted.
        // A subsequent full sync will remove this credential again.
        vaultDao.restoreCredential(id)
    }

    override suspend fun permanentlyDeleteCredential(id: String) {
        // This is the only place we call the API to delete — when the user explicitly
        // chooses to remove the credential forever from the trash.
        val idLong = id.toLongOrNull()
        if (idLong != null) {
            val response = try {
                apiService.deleteEntry(idLong)
            } catch (e: Exception) {
                throw Exception("Failed to delete credential: ${e.message}")
            }
            // 204 = deleted now, 404 = already gone (e.g. deleted via another device).
            // Both are acceptable — remove from Room either way.
            if (!response.isSuccessful && response.code() != 404) {
                throw Exception("Failed to delete credential: HTTP ${response.code()}")
            }
        }
        vaultDao.permanentlyDeleteCredential(id)
    }

    override suspend fun sync() {
        // Full sync: clear non-trashed entries and replace with remote data.
        // Trashed entries (deletedAt IS NOT NULL) are preserved by clearAll().
        // androidPackageName is now persisted server-side, so no manual preservation needed.
        val result = handleApiCall { apiService.getEntries() }
        if (result.isSuccess) {
            val remoteEntries = result.getOrNull() ?: emptyList()
            vaultDao.clearAll()
            vaultDao.insertCredentials(remoteEntries.map { it.toDomain().toEntity() })
        }
    }

    private suspend fun <T> handleApiCall(call: suspend () -> Response<T>): Result<T> {
        return try {
            val response = call()
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else if (response.isSuccessful && response.code() == 204) {
                 // Handle NoContent for deletes
                @Suppress("UNCHECKED_CAST")
                Result.success(Unit as T)
            } else {
                Result.failure(Exception("API Error: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
