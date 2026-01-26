package com.wilove.vaulten.ui.credentials

import com.wilove.vaulten.domain.model.Credential

/**
 * Immutable UI state for the Credentials List screen.
 */
data class CredentialsListUiState(
    val credentials: List<Credential> = emptyList(),
    val searchQuery: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)
