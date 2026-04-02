package com.wilove.vaulten.ui.trash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wilove.vaulten.domain.model.Credential
import com.wilove.vaulten.domain.usecase.GetDeletedCredentialsUseCase
import com.wilove.vaulten.domain.usecase.PermanentlyDeleteCredentialUseCase
import com.wilove.vaulten.domain.usecase.RestoreCredentialUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class TrashUiState(
    val credentials: List<Credential> = emptyList(),
    val isLoading: Boolean = true,
    val errorMessage: String? = null
)

class TrashViewModel(
    private val getDeletedCredentialsUseCase: GetDeletedCredentialsUseCase,
    private val restoreCredentialUseCase: RestoreCredentialUseCase,
    private val permanentlyDeleteCredentialUseCase: PermanentlyDeleteCredentialUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(TrashUiState())
    val uiState: StateFlow<TrashUiState> = _uiState.asStateFlow()

    init {
        observeDeletedCredentials()
    }

    private fun observeDeletedCredentials() {
        viewModelScope.launch {
            getDeletedCredentialsUseCase()
                .catch { e ->
                    _uiState.update { it.copy(isLoading = false, errorMessage = e.message) }
                }
                .collect { credentials ->
                    _uiState.update {
                        it.copy(credentials = credentials, isLoading = false, errorMessage = null)
                    }
                }
        }
    }

    fun restore(credentialId: String) {
        viewModelScope.launch {
            try {
                restoreCredentialUseCase(credentialId)
            } catch (e: Exception) {
                _uiState.update { it.copy(errorMessage = e.message ?: "No se pudo restaurar la credencial. Inténtalo de nuevo.") }
            }
        }
    }

    fun permanentlyDelete(credentialId: String) {
        viewModelScope.launch {
            try {
                permanentlyDeleteCredentialUseCase(credentialId)
            } catch (e: Exception) {
                _uiState.update { it.copy(errorMessage = e.message ?: "No se pudo eliminar la credencial. Inténtalo de nuevo.") }
            }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}
