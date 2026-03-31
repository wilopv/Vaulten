package com.wilove.vaulten.ui.credentials

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wilove.vaulten.domain.model.Credential
import com.wilove.vaulten.domain.usecase.CreateCredentialUseCase
import com.wilove.vaulten.domain.usecase.GetCredentialByIdUseCase
import com.wilove.vaulten.domain.usecase.UpdateCredentialUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel for the Create/Edit Credential screen.
 * Manages credential creation and editing.
 */
class CreateEditCredentialViewModel(
    private val createCredentialUseCase: CreateCredentialUseCase,
    private val updateCredentialUseCase: UpdateCredentialUseCase,
    private val getCredentialByIdUseCase: GetCredentialByIdUseCase? = null
) : ViewModel() {
    private val _uiState = MutableStateFlow(CreateEditCredentialUiState())
    val uiState: StateFlow<CreateEditCredentialUiState> = _uiState.asStateFlow()

    /**
     * Initializes the screen for editing an existing credential.
     */
    fun loadCredentialForEditing(credentialId: String) {
        if (getCredentialByIdUseCase == null) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                val credential = getCredentialByIdUseCase(credentialId)
                if (credential != null) {
                    _uiState.update {
                        it.copy(
                            credentialId = credential.id,
                            name = credential.name,
                            username = credential.username,
                            password = credential.password,
                            url = credential.url ?: "",
                            isLoading = false,
                            isEditMode = true
                        )
                    }
                }
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
     * Updates the name field.
     */
    fun onNameChange(name: String) {
        _uiState.update { it.copy(name = name, nameError = null) }
    }

    /**
     * Updates the username field.
     */
    fun onUsernameChange(username: String) {
        _uiState.update { it.copy(username = username, usernameError = null) }
    }

    /**
     * Updates the password field.
     */
    fun onPasswordChange(password: String) {
        _uiState.update { it.copy(password = password, passwordError = null) }
    }

    /**
     * Updates the URL field.
     */
    fun onUrlChange(url: String) {
        _uiState.update { it.copy(url = url) }
    }

    /**
     * Saves the credential (creates or updates depending on mode).
     */
    fun saveCredential() {
        val state = _uiState.value

        // Validate fields
        val validationErrors = state.getValidationErrors()
        if (validationErrors.isNotEmpty()) {
            _uiState.update {
                it.copy(
                    nameError = validationErrors["name"],
                    usernameError = validationErrors["username"],
                    passwordError = validationErrors["password"]
                )
            }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, errorMessage = null) }
            try {
                val credential = Credential(
                    id = state.credentialId ?: "",
                    name = state.name,
                    username = state.username,
                    password = state.password,
                    url = state.url.takeIf { it.isNotBlank() },
                    lastModified = System.currentTimeMillis()
                )

                if (state.isEditMode && state.credentialId != null) {
                    updateCredentialUseCase(credential)
                } else {
                    createCredentialUseCase(credential)
                }

                _uiState.update {
                    it.copy(
                        isSaving = false,
                        savedSuccessfully = true
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isSaving = false,
                        errorMessage = "Failed to save credential: ${e.message}"
                    )
                }
            }
        }
    }
}
