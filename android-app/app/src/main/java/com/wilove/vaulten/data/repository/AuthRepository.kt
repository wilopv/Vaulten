package com.wilove.vaulten.data.repository

import com.wilove.vaulten.data.remote.AuthApiService
import com.wilove.vaulten.data.remote.model.LoginRequest
import com.wilove.vaulten.data.remote.model.RegisterRequest
import com.wilove.vaulten.data.local.TokenManager
import retrofit2.Response

/**
 * Repository handling authentication logic and token lifecycle.
 */
class AuthRepository(
    private val authApiService: AuthApiService,
    private val tokenManager: TokenManager
) {

    /**
     * Registers a new user and returns a Result.
     */
    suspend fun register(request: RegisterRequest): Result<Unit> {
        return try {
            val response = authApiService.register(request)
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
    suspend fun login(request: LoginRequest): Result<String> {
        return try {
            val response = authApiService.login(request)
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
    fun logout() {
        tokenManager.deleteToken()
    }

    /**
     * Returns the currently stored token if it exists.
     */
    fun getLoggedToken(): String? = tokenManager.getToken()
}
