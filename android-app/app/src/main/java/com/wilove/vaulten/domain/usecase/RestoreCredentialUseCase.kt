package com.wilove.vaulten.domain.usecase

import javax.inject.Inject
import com.wilove.vaulten.domain.repository.VaultRepository

class RestoreCredentialUseCase @Inject constructor(
    private val repository: VaultRepository
) {
    suspend operator fun invoke(credentialId: String) = repository.restoreCredential(credentialId)
}
