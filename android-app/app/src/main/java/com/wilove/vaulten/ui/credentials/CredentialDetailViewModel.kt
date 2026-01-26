package com.wilove.vaulten.ui.credentials

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wilove.vaulten.domain.usecase.GetCredentialByIdUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel for the Credential Detail screen.
 * Manages loading and displaying a single credential.
 */
class CredentialDetailViewModel(
    private val getCredentialByIdUseCase: GetCredentialByIdUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(CredentialDetailUiState())
    val uiState: StateFlow<CredentialDetailUiState> = _uiState.asStateFlow()

    /**
     * Loads a credential by its ID.
     */
    fun loadCredential(credentialId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                val credential = getCredentialByIdUseCase(credentialId)
                _uiState.update { it.copy(credential = credential, isLoading = false) }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Failed to load credential: ${e.message}"
                    )
                }
            }
        }
    }

    /**
     * Toggles password visibility.
     */
    fun togglePasswordVisibility() {
        _uiState.update { it.copy(passwordVisible = !it.passwordVisible) }
    }

    /**
     * Shows a temporary "copied" indicator for a field.
     */
    fun markFieldAsCopied(fieldName: String) {
        _uiState.update { it.copy(copiedField = fieldName) }
        viewModelScope.launch {
            kotlinx.coroutines.delay(2000) // Show for 2 seconds
            _uiState.update { it.copy(copiedField = null) }
        }
    }
}
