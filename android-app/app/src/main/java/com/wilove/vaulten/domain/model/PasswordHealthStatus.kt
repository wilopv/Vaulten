package com.wilove.vaulten.domain.model

data class PasswordHealthStatus(
    val isDuplicate: Boolean = false,
    val weaknesses: List<PasswordWeakness> = emptyList()
) {
    val isWeak: Boolean get() = weaknesses.isNotEmpty()
    val hasIssues: Boolean get() = isDuplicate || isWeak
}

enum class PasswordWeakness {
    TOO_SHORT,
    NO_UPPERCASE,
    NO_LOWERCASE,
    NO_DIGIT,
    NO_SPECIAL_CHAR
}
