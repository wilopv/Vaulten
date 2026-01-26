package com.wilove.vaulten.domain.usecase

import com.wilove.vaulten.domain.model.Credential
import com.wilove.vaulten.domain.repository.VaultRepository

/**
 * Use case to create a new credential.
 */
class CreateCredentialUseCase(
    private val repository: VaultRepository
) {
    suspend operator fun invoke(credential: Credential) {
        return repository.saveCredential(credential)
    }
}
