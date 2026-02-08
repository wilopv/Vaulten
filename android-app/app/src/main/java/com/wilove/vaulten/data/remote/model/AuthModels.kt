package com.wilove.vaulten.data.remote.model

import kotlinx.serialization.Serializable

/**
 * Request model for user login.
 */
@Serializable
data class LoginRequest(
    val username: String,
    val password: String
)

/**
 * Request model for user registration.
 */
@Serializable
data class RegisterRequest(
    val username: String,
    val email: String,
    val password: String
)

/**
 * Response model containing the JWT token after successful authentication.
 */
@Serializable
data class AuthResponse(
    val token: String
)
