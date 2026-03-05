package com.wilove.vaulten.ui.credentials

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wilove.vaulten.domain.model.Credential
import com.wilove.vaulten.domain.usecase.GetAllCredentialsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel for the Credentials List screen.
 * Observes credentials from local storage and handles search filtering.
 */
class CredentialsListViewModel(
    private val getAllCredentialsUseCase: GetAllCredentialsUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(CredentialsListUiState())
    val uiState: StateFlow<CredentialsListUiState> = _uiState.asStateFlow()

    private var cachedCredentials: List<Credential> = emptyList()

    init {
        observeCredentials()
    }

    /**
     * Observes the reactive flow of credentials.
     */
    private fun observeCredentials() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            getAllCredentialsUseCase()
                .catch { e ->
                    _uiState.update { it.copy(isLoading = false, errorMessage = e.message) }
                }
                .collect { credentials ->
                    cachedCredentials = credentials
                    applyFilter(_uiState.value.searchQuery, credentials, isLoading = false)
                }
        }
    }

    /** Updates the search query and filters the list. */
    fun onSearchQueryChange(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        applyFilter(query, cachedCredentials)
    }

    /** Manual refresh (if needed, but Flow handles auto-updates). */
    fun refresh() {
        // Since we are using Flow, updates happen automatically from Room.
        // If we wanted to force a remote sync, we'd need a SyncUseCase.
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
