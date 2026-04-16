package com.wilove.vaulten.ui.credentials

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import androidx.lifecycle.viewModelScope
import com.wilove.vaulten.domain.usecase.DeleteCredentialUseCase
import com.wilove.vaulten.domain.usecase.GetAllCredentialsUseCase
import com.wilove.vaulten.domain.usecase.GetCredentialByIdUseCase
import com.wilove.vaulten.domain.usecase.PasswordHealthUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel for the Credential Detail screen.
 * Manages loading and displaying a single credential.
 */
@HiltViewModel
class CredentialDetailViewModel @Inject constructor(
    private val getCredentialByIdUseCase: GetCredentialByIdUseCase,
    private val deleteCredentialUseCase: DeleteCredentialUseCase,
    private val getAllCredentialsUseCase: GetAllCredentialsUseCase,
    private val passwordHealthUseCase: PasswordHealthUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(CredentialDetailUiState())
    val uiState: StateFlow<CredentialDetailUiState> = _uiState.asStateFlow()

    private val _navigateBack = MutableStateFlow(false)
    val navigateBack: StateFlow<Boolean> = _navigateBack.asStateFlow()

    /**
     * Loads a credential by its ID and evaluates its password health.
     */
    fun loadCredential(credentialId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                val credential = getCredentialByIdUseCase(credentialId)
                val allCredentials = getAllCredentialsUseCase().first()
                val health = passwordHealthUseCase(allCredentials)[credentialId]
                _uiState.update {
                    it.copy(credential = credential, isLoading = false, passwordHealth = health)
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = e.message ?: "No se pudo cargar la credencial. Inténtalo de nuevo."
                    )
                }
            }
        }
    }

    /**
     * Deletes the credential and emits navigateBack when successful.
     */
    fun deleteCredential(credentialId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isDeleting = true, deleteError = null) }
            try {
                deleteCredentialUseCase(credentialId)
                _uiState.update { it.copy(isDeleting = false) }
                _navigateBack.value = true
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isDeleting = false,
                        deleteError = e.message ?: "No se pudo eliminar la credencial. Inténtalo de nuevo."
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
