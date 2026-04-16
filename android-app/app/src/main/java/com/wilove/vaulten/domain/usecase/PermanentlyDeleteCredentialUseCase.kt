package com.wilove.vaulten.domain.usecase

import javax.inject.Inject
import com.wilove.vaulten.domain.repository.VaultRepository

class PermanentlyDeleteCredentialUseCase @Inject constructor(
    private val repository: VaultRepository
) {
    suspend operator fun invoke(credentialId: String) =
        repository.permanentlyDeleteCredential(credentialId)
}
