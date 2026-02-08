package com.wilove.vaulten.data.remote

import com.wilove.vaulten.data.remote.model.AuthResponse
import com.wilove.vaulten.data.remote.model.LoginRequest
import com.wilove.vaulten.data.remote.model.RegisterRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

/**
 * Retrofit service for authentication endpoints.
 */
interface AuthApiService {
    /**
     * Registers a new user.
     */
    @POST("api/auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<Unit>

    /**
     * Authenticates a user and returns a JWT token.
     */
    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequest): Response<AuthResponse>
}
