package com.wilove.vaulten.domain.usecase

import com.wilove.vaulten.domain.repository.VaultRepository

/**
 * Use case to delete an existing credential.
 */
class DeleteCredentialUseCase(
    private val repository: VaultRepository
) {
    suspend operator fun invoke(credentialId: String) {
        return repository.deleteCredential(credentialId)
    }
}
