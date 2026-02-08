package com.wilove.vaulten.di

import android.content.Context
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.wilove.vaulten.data.local.TokenManager
import com.wilove.vaulten.data.remote.AuthApiService
import com.wilove.vaulten.data.remote.AuthInterceptor
import com.wilove.vaulten.data.remote.VaultApiService
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import timber.log.Timber

/**
 * Dependency Injection module (singleton object) for Network related components.
 */
object NetworkModule {

    private const val BASE_URL = "http://localhost:8080/"
    private val json = Json { ignoreUnknownKeys = true }
    private val contentType = "application/json".toMediaType()

    /**
     * Provides the TokenManager instance.
     */
    fun provideTokenManager(context: Context): TokenManager {
        return TokenManager(context)
    }

    /**
     * Configures and provides the OkHttpClient with AuthInterceptor and Logging.
     */
    fun provideOkHttpClient(tokenManager: TokenManager): OkHttpClient {
        val logging = HttpLoggingInterceptor { message -> Timber.d(message) }
        logging.level = HttpLoggingInterceptor.Level.BODY

        return OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor(tokenManager))
            .addInterceptor(logging)
            .build()
    }

    // Shared Retrofit builder configuration
    private val retrofitBuilder: Retrofit.Builder by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(json.asConverterFactory(contentType))
    }

    /**
     * Provides the AuthApiService instance.
     */
    fun provideAuthApiService(okHttpClient: OkHttpClient): AuthApiService {
        return retrofitBuilder
            .client(okHttpClient)
            .build()
            .create(AuthApiService::class.java)
    }

    /**
     * Provides the VaultApiService instance.
     */
    fun provideVaultApiService(okHttpClient: OkHttpClient): VaultApiService {
        return retrofitBuilder
            .client(okHttpClient)
            .build()
            .create(VaultApiService::class.java)
    }
}
