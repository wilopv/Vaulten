package com.wilove.vaulten.domain.usecase

import com.wilove.vaulten.domain.model.Credential
import com.wilove.vaulten.domain.model.PasswordHealthStatus
import com.wilove.vaulten.domain.model.PasswordWeakness
import javax.inject.Inject

/**
 * Analyzes the health of passwords across a list of credentials.
 * Does not access the repository; receives the list already loaded.
 */
class PasswordHealthUseCase @Inject constructor() {
    operator fun invoke(credentials: List<Credential>): Map<String, PasswordHealthStatus> {
        val countByPassword = credentials.groupingBy { it.password }.eachCount()
        return credentials.associate { credential ->
            val isDuplicate = (countByPassword[credential.password] ?: 0) > 1
            credential.id to PasswordHealthStatus(
                isDuplicate = isDuplicate,
                weaknesses = analyzeWeaknesses(credential.password)
            )
        }
    }

    private fun analyzeWeaknesses(password: String): List<PasswordWeakness> {
        val weaknesses = mutableListOf<PasswordWeakness>()
        if (password.length < 8) weaknesses.add(PasswordWeakness.TOO_SHORT)
        if (!password.any { it.isUpperCase() }) weaknesses.add(PasswordWeakness.NO_UPPERCASE)
        if (!password.any { it.isLowerCase() }) weaknesses.add(PasswordWeakness.NO_LOWERCASE)
        if (!password.any { it.isDigit() }) weaknesses.add(PasswordWeakness.NO_DIGIT)
        if (!password.any { !it.isLetterOrDigit() }) weaknesses.add(PasswordWeakness.NO_SPECIAL_CHAR)
        return weaknesses
    }
}
