package com.wilove.vaulten.ui.login

/**
 * Immutable UI state for the Login screen.
 * All visual elements read from this state so the UI remains stateless.
 */
data class LoginUiState(
    val email: String = "",
    val masterPassword: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val biometricEnabled: Boolean = true,
    val remainingAttempts: Int = 5,
    val maxAttempts: Int = 5,
    val isLockedOut: Boolean = false
)
