package com.wilove.vaulten.ui.passwordgenerator

/**
 * Immutable UI state for the Password Generator screen.
 */
data class PasswordGeneratorUiState(
    val generatedPassword: String = "",
    val length: Int = 16,
    val useUppercase: Boolean = true,
    val useLowercase: Boolean = true,
    val useNumbers: Boolean = true,
    val useSymbols: Boolean = true,
    val copiedPassword: Boolean = false,
    val errorMessage: String? = null
)

/**
 * Calculates password strength level.
 */
fun PasswordGeneratorUiState.getPasswordStrength(): PasswordStrength {
    return when {
        generatedPassword.length < 8 -> PasswordStrength.WEAK
        generatedPassword.length < 12 -> PasswordStrength.FAIR
        generatedPassword.length < 16 -> PasswordStrength.GOOD
        else -> PasswordStrength.STRONG
    }
}

/**
 * Password strength levels.
 */
enum class PasswordStrength(val displayName: String) {
    WEAK("Weak"),
    FAIR("Fair"),
    GOOD("Good"),
    STRONG("Strong")
}
