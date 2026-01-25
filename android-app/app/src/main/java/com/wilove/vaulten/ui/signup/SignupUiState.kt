package com.wilove.vaulten.ui.signup

/**
 * Immutable UI state for the Signup screen.
 * All visual elements read from this state so the UI remains stateless.
 */
data class SignupUiState(
    val fullName: String = "",
    val email: String = "",
    val masterPassword: String = "",
    val confirmPassword: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)
