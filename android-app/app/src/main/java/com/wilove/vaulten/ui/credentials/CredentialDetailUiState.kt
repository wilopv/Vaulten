package com.wilove.vaulten.ui.credentials

import com.wilove.vaulten.domain.model.Credential

/**
 * Immutable UI state for the Credential Detail screen.
 */
data class CredentialDetailUiState(
    val credential: Credential? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val passwordVisible: Boolean = false,
    val copiedField: String? = null
)
