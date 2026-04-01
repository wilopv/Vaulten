package com.wilove.vaulten.data.remote

import com.wilove.vaulten.data.local.TokenManager
import com.wilove.vaulten.data.remote.model.AuthResponse
import kotlinx.serialization.json.Json
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.RequestBody.Companion.toRequestBody
import java.util.concurrent.TimeUnit

/**
 * OkHttp Interceptor that:
 * 1. Injects the JWT Bearer token into every outgoing request.
 * 2. On 401: attempts a token refresh and retries the original request once.
 */
class AuthInterceptor(
    private val tokenManager: TokenManager,
    private val baseUrl: String
) : Interceptor {

    // Bare client used only for the refresh call — no interceptors to avoid recursion.
    private val refreshClient = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    private val json = Json { ignoreUnknownKeys = true }

    override fun intercept(chain: Interceptor.Chain): Response {
        val token = tokenManager.getToken()
        val request = chain.request().newBuilder()
            .apply { if (token != null) header("Authorization", "Bearer $token") }
            .build()

        val response = chain.proceed(request)

        // If 401 and it's not the refresh endpoint itself, try to refresh and retry.
        if (response.code == 401 && !request.url.toString().contains("auth/refresh")) {
            val newToken = tryRefresh() ?: return response
            response.close()
            return chain.proceed(
                chain.request().newBuilder()
                    .header("Authorization", "Bearer $newToken")
                    .build()
            )
        }

        return response
    }

    private fun tryRefresh(): String? {
        val expiredToken = tokenManager.getToken() ?: return null
        return try {
            val refreshRequest = Request.Builder()
                .url("${baseUrl}api/auth/refresh")
                .post("".toRequestBody("application/json".toMediaType()))
                .header("Authorization", "Bearer $expiredToken")
                .build()

            refreshClient.newCall(refreshRequest).execute().use { response ->
                if (!response.isSuccessful) return null
                response.body?.string()?.let { body ->
                    json.decodeFromString<AuthResponse>(body)
                        .token
                        .also { tokenManager.saveToken(it) }
                }
            }
        } catch (e: Exception) {
            null
        }
    }
}
