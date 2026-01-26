package com.wilove.vaulten.domain.usecase

/**
 * Use case to generate secure random passwords.
 */
class GeneratePasswordUseCase {
    operator fun invoke(
        length: Int,
        useUppercase: Boolean,
        useLowercase: Boolean,
        useNumbers: Boolean,
        useSymbols: Boolean
    ): String {
        if (!useUppercase && !useLowercase && !useNumbers && !useSymbols) {
            throw IllegalArgumentException("At least one character type must be selected")
        }

        val uppercaseChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
        val lowercaseChars = "abcdefghijklmnopqrstuvwxyz"
        val numberChars = "0123456789"
        val symbolChars = "!@#$%^&*()_+-=[]{}|;:,.<>?"

        val availableChars = StringBuilder()
        if (useUppercase) availableChars.append(uppercaseChars)
        if (useLowercase) availableChars.append(lowercaseChars)
        if (useNumbers) availableChars.append(numberChars)
        if (useSymbols) availableChars.append(symbolChars)

        return (1..length)
            .map { availableChars[kotlin.random.Random.nextInt(availableChars.length)] }
            .joinToString("")
    }
}
