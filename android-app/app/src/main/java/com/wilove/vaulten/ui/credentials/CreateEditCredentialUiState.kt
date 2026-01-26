package com.wilove.vaulten.ui.credentials

import com.wilove.vaulten.domain.model.Credential

/**
 * Immutable UI state for the Create/Edit Credential screen.
 */
data class CreateEditCredentialUiState(
    val credentialId: String? = null,
    val name: String = "",
    val username: String = "",
    val password: String = "",
    val url: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isSaving: Boolean = false,
    val nameError: String? = null,
    val usernameError: String? = null,
    val passwordError: String? = null,
    val isEditMode: Boolean = false,
    val savedSuccessfully: Boolean = false
)

/**
 * Validates credential fields.
 */
fun CreateEditCredentialUiState.getValidationErrors(): Map<String, String?> {
    val errors = mutableMapOf<String, String?>()

    if (name.isBlank()) {
        errors["name"] = "Name is required"
    }

    if (username.isBlank()) {
        errors["username"] = "Username is required"
    }

    if (password.isBlank()) {
        errors["password"] = "Password is required"
    }

    return errors
}

/**
 * Checks if all required fields are valid.
 */
fun CreateEditCredentialUiState.isValid(): Boolean {
    return name.isNotBlank() &&
            username.isNotBlank() &&
            password.isNotBlank() &&
            nameError == null &&
            usernameError == null &&
            passwordError == null
}
