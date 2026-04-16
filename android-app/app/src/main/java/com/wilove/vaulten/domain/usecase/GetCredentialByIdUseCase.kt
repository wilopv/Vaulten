package com.wilove.vaulten.domain.usecase

import javax.inject.Inject
import com.wilove.vaulten.domain.model.Credential
import com.wilove.vaulten.domain.repository.VaultRepository

/**
 * Use case to retrieve a specific credential by its ID.
 */
class GetCredentialByIdUseCase @Inject constructor(
    private val repository: VaultRepository
) {
    suspend operator fun invoke(credentialId: String): Credential? {
        return repository.getCredentialById(credentialId)
    }
}
