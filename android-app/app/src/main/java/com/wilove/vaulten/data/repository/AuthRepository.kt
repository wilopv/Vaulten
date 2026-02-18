package com.wilove.vaulten.data.repository

import com.wilove.vaulten.data.remote.AuthApiService
import com.wilove.vaulten.data.remote.model.LoginRequest
import com.wilove.vaulten.data.remote.model.RegisterRequest
import com.wilove.vaulten.data.local.TokenManager
import com.wilove.vaulten.domain.repository.AuthRepository
import retrofit2.Response

/**
 * Repository handling authentication logic and token lifecycle.
 * Implements the domain [AuthRepository] interface.
 */
class AuthRepositoryImpl(
    private val authApiService: AuthApiService,
    private val tokenManager: TokenManager
) : AuthRepository {

    /**
     * Registers a new user and returns a Result.
     */
    override suspend fun register(username: String, email: String, password: String): Result<Unit> {
        return try {
            val response = authApiService.register(RegisterRequest(username, email, password))
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Registration failed: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Logs in the user, saves the token on success, and returns a Result.
     */
    override suspend fun login(username: String, password: String): Result<String> {
        return try {
            val response = authApiService.login(LoginRequest(username, password))
            if (response.isSuccessful && response.body() != null) {
                val token = response.body()!!.token
                tokenManager.saveToken(token)
                Result.success(token)
            } else {
                Result.failure(Exception("Login failed: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Clears the stored token from the device.
     */
    override fun logout() {
        tokenManager.deleteToken()
    }

    /**
     * Returns the currently stored token if it exists.
     */
    override fun getLoggedToken(): String? = tokenManager.getToken()
}
