package com.wilove.vaulten.domain.repository

import com.wilove.vaulten.domain.model.Credential
import com.wilove.vaulten.domain.model.SecurityAlert

/**
 * Repository interface for vault operations.
 * Defines the contract for accessing and managing credentials and security data.
 *
 * This interface abstracts the data layer from the domain layer,
 * allowing for different implementations (mock, local database, remote API).
 */
interface VaultRepository {
    /**
     * Retrieves the most recently accessed credentials.
     *
     * @param limit Maximum number of credentials to return
     * @return List of recent credentials
     */
    suspend fun getRecentCredentials(limit: Int = 5): List<Credential>

    /**
     * Retrieves all active security alerts.
     *
     * @return List of security alerts
     */
    suspend fun getSecurityAlerts(): List<SecurityAlert>

    /**
     * Retrieves all stored credentials.
     *
     * @return List of all credentials
     */
    suspend fun getAllCredentials(): List<Credential>

    /**
     * Retrieves a specific credential by ID.
     *
     * @param id Credential identifier
     * @return The credential if found, null otherwise
     */
    suspend fun getCredentialById(id: String): Credential?

    /**
     * Saves or updates a credential.
     *
     * @param credential The credential to save
     */
    suspend fun saveCredential(credential: Credential)

    /**
     * Deletes a credential.
     *
     * @param id Credential identifier
     */
    suspend fun deleteCredential(id: String)
}
