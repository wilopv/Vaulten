package com.wilove.vaulten.domain.usecase

import com.wilove.vaulten.domain.model.Credential
import com.wilove.vaulten.domain.repository.VaultRepository

/**
 * Use case for retrieving all credentials.
 *
 * @property repository The vault repository for data access
 */
class GetAllCredentialsUseCase(
    private val repository: VaultRepository
) {
    /**
     * Executes the use case to retrieve all credentials.
     *
     * @return List of stored credentials
     */
    suspend operator fun invoke(): List<Credential> = repository.getAllCredentials()
}
