package com.wilove.vaulten.data.repository

import com.wilove.vaulten.data.remote.VaultApiService
import com.wilove.vaulten.data.remote.mapper.toDomain
import com.wilove.vaulten.data.remote.model.*
import com.wilove.vaulten.domain.model.Credential
import com.wilove.vaulten.domain.model.SecurityAlert
import com.wilove.vaulten.domain.repository.VaultRepository
import retrofit2.Response

/**
 * Repository responsible for managing vault entries and synchronization.
 * Implements the domain [VaultRepository] interface using [VaultApiService].
 */
class VaultRepositoryImpl(
    private val apiService: VaultApiService
) : VaultRepository {

    override suspend fun getRecentCredentials(limit: Int): List<Credential> {
        val result = handleApiCall { apiService.getEntries() }
        return result.getOrDefault(emptyList())
            .map { it.toDomain() }
            .sortedByDescending { it.lastModified }
            .take(limit)
    }

    override suspend fun getSecurityAlerts(): List<SecurityAlert> {
        // TODO: Implement real security alerts from API if available
        return emptyList()
    }

    override suspend fun getAllCredentials(): List<Credential> {
        val result = handleApiCall { apiService.getEntries() }
        return result.getOrDefault(emptyList()).map { it.toDomain() }
    }

    override suspend fun getCredentialById(id: String): Credential? {
        val idLong = id.toLongOrNull() ?: return null
        val result = handleApiCall { apiService.getEntry(idLong) }
        return result.getOrNull()?.toDomain()
    }

    override suspend fun saveCredential(credential: Credential) {
        val idLong = credential.id.toLongOrNull()
        val request = VaultEntryRequest(
            name = credential.name,
            username = credential.username,
            password = credential.password,
            url = credential.url,
            type = VaultType.LOGIN, // Default for now
            category = "General"
        )

        if (idLong == null || idLong == 0L) {
            apiService.createEntry(request)
        } else {
            apiService.updateEntry(idLong, request)
        }
    }

    override suspend fun deleteCredential(id: String) {
        val idLong = id.toLongOrNull() ?: return
        apiService.deleteEntry(idLong)
    }

    /**
     * Performs an incremental sync (not part of the domain interface but used internally or in specific use cases).
     */
    suspend fun sync(since: String): Result<SyncResponse> {
        return handleApiCall { apiService.sync(since) }
    }

    /**
     * Helper function to encapsulate API service calls and map them to [Result].
     */
    private suspend fun <T> handleApiCall(call: suspend () -> Response<T>): Result<T> {
        return try {
            val response = call()
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("API Error: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
