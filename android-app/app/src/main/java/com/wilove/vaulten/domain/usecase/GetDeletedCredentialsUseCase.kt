package com.wilove.vaulten.domain.usecase

import com.wilove.vaulten.domain.model.Credential
import com.wilove.vaulten.domain.repository.VaultRepository
import kotlinx.coroutines.flow.Flow

class GetDeletedCredentialsUseCase(
    private val repository: VaultRepository
) {
    operator fun invoke(): Flow<List<Credential>> = repository.getDeletedCredentials()
}
