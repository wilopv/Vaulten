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
                // Save to local cache with the ID provided by the server
                vaultDao.insertCredential(remoteEntry.toDomain().toEntity())
            }
        } else {
            throw result.exceptionOrNull() ?: Exception("Failed to save credential")
        }
    }

    override suspend fun deleteCredential(id: String) {
        val idLong = id.toLongOrNull() ?: return
        val result = handleApiCall { apiService.deleteEntry(idLong) }
        if (result.isSuccess) {
            vaultDao.deleteCredential(id)
        } else {
            throw result.exceptionOrNull() ?: Exception("Failed to delete credential")
        }
    }

    override suspend fun sync() {
        // For now, full sync (get all entries and replace local)
        // In the future, use the "since" timestamp for incremental sync
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
