package com.wilove.vaulten.ui.credentials

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wilove.vaulten.domain.model.Credential
import com.wilove.vaulten.domain.usecase.GetAllCredentialsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel for the Credentials List screen.
 * Manages credential data and search filtering.
 */
class CredentialsListViewModel(
    private val getAllCredentialsUseCase: GetAllCredentialsUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(CredentialsListUiState())
    val uiState: StateFlow<CredentialsListUiState> = _uiState.asStateFlow()

    private var cachedCredentials: List<Credential> = emptyList()

    init {
        loadCredentials()
    }

    /** Updates the search query and filters the list. */
    fun onSearchQueryChange(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        applyFilter(query, cachedCredentials)
    }

    /** Loads all credentials from the repository. */
    fun loadCredentials() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                val credentials = getAllCredentialsUseCase()
                cachedCredentials = credentials
                applyFilter(_uiState.value.searchQuery, credentials, isLoading = false)
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Failed to load credentials: ${e.message}"
                    )
                }
            }
        }
    }

    /** Refreshes the credentials list. */
    fun refresh() {
        loadCredentials()
    }

    private fun applyFilter(
        query: String,
        credentials: List<Credential>,
        isLoading: Boolean = _uiState.value.isLoading
    ) {
        val trimmedQuery = query.trim()
        val filtered = if (trimmedQuery.isBlank()) {
            credentials
        } else {
            credentials.filter { credential ->
                credential.name.contains(trimmedQuery, ignoreCase = true) ||
                    credential.username.contains(trimmedQuery, ignoreCase = true) ||
                    (credential.url?.contains(trimmedQuery, ignoreCase = true) == true)
            }
        }
        _uiState.update {
            it.copy(
                credentials = filtered,
                isLoading = isLoading,
                errorMessage = null
            )
        }
    }
}
