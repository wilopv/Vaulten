package com.wilove.vaulten.domain.repository

/**
 * Domain interface for authentication operations.
 */
interface AuthRepository {
    /**
     * Registers a new user.
     */
    suspend fun register(username: String, email: String, password: String): Result<Unit>

    /**
     * Authenticates a user and returns a token.
     */
    suspend fun login(username: String, password: String): Result<String>

    /**
     * Clears the authentication state.
     */
    fun logout()

    /**
     * Retrieves the current authentication token if available.
     */
    fun getLoggedToken(): String?
}
