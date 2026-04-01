package com.wilove.vaulten.domain.usecase

import com.wilove.vaulten.domain.repository.VaultRepository

class RestoreCredentialUseCase(
    private val repository: VaultRepository
) {
    suspend operator fun invoke(credentialId: String) = repository.restoreCredential(credentialId)
}
