package com.wilove.vaulten.ui.credentials

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wilove.vaulten.domain.model.Credential
import com.wilove.vaulten.domain.model.CredentialFilter
import com.wilove.vaulten.domain.model.PasswordHealthStatus
import com.wilove.vaulten.domain.usecase.GetAllCredentialsUseCase
import com.wilove.vaulten.domain.usecase.PasswordHealthUseCase
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
    private val getAllCredentialsUseCase: GetAllCredentialsUseCase,
    private val passwordHealthUseCase: PasswordHealthUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(CredentialsListUiState())
    val uiState: StateFlow<CredentialsListUiState> = _uiState.asStateFlow()

    private var cachedCredentials: List<Credential> = emptyList()
    private var cachedHealth: Map<String, PasswordHealthStatus> = emptyMap()

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
                    cachedHealth = passwordHealthUseCase(credentials)
                    applyFilter(_uiState.value.searchQuery, _uiState.value.activeFilter, isLoading = false)
                }
        }
    }

    /** Updates the search query and re-applies all active filters. */
    fun onSearchQueryChange(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        applyFilter(query, _uiState.value.activeFilter)
    }

    /** Applies a new filter, keeping the current search query. */
    fun applyFilter(filter: CredentialFilter) {
        _uiState.update { it.copy(activeFilter = filter) }
        applyFilter(_uiState.value.searchQuery, filter)
    }

    /** Resets the filter to default, keeping the current search query. */
    fun clearFilter() {
        val emptyFilter = CredentialFilter()
        _uiState.update { it.copy(activeFilter = emptyFilter) }
        applyFilter(_uiState.value.searchQuery, emptyFilter)
    }

    /** Manual refresh (if needed, but Flow handles auto-updates). */
    fun refresh() {
        // Since we are using Flow, updates happen automatically from Room.
        // If we wanted to force a remote sync, we'd need a SyncUseCase.
    }

    private fun applyFilter(
        query: String,
        filter: CredentialFilter,
        isLoading: Boolean = _uiState.value.isLoading
    ) {
        val trimmedQuery = query.trim()
        val filtered = cachedCredentials.filter { credential ->
            val matchesQuery = trimmedQuery.isBlank() ||
                credential.name.contains(trimmedQuery, ignoreCase = true) ||
                credential.username.contains(trimmedQuery, ignoreCase = true) ||
                credential.url?.contains(trimmedQuery, ignoreCase = true) == true

            val matchesDomain = filter.domain.isNullOrBlank() ||
                credential.url?.contains(filter.domain, ignoreCase = true) == true

            val matchesAfter = filter.modifiedAfter == null ||
                credential.lastModified >= filter.modifiedAfter

            val matchesBefore = filter.modifiedBefore == null ||
                credential.lastModified <= filter.modifiedBefore

            matchesQuery && matchesDomain && matchesAfter && matchesBefore
        }
        _uiState.update {
            it.copy(
                credentials = filtered,
                passwordHealth = cachedHealth,
                isLoading = isLoading,
                errorMessage = null
            )
        }
    }
}
