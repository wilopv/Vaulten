package com.wilove.vaulten.data.remote

import retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.wilove.vaulten.data.remote.model.LoginRequest
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit

class AuthApiServiceTest {

    private lateinit var server: MockWebServer
    private lateinit var apiService: AuthApiService

    private val json = Json { ignoreUnknownKeys = true }
    private val contentType = "application/json".toMediaType()

    @Before
    fun setup() {
        server = MockWebServer()
        apiService = Retrofit.Builder()
            .baseUrl(server.url("/"))
            .addConverterFactory(json.asConverterFactory(contentType))
            .build()
            .create(AuthApiService::class.java)
    }

    @After
    fun tearDown() {
        server.shutdown()
    }

    @Test
    fun `login returns success response with token`() = runTest {
        val mockResponse = MockResponse()
            .setResponseCode(200)
            .setBody("""{"token": "fake-jwt-token"}""")
        server.enqueue(mockResponse)

        val response = apiService.login(LoginRequest("user", "pass"))

        assertTrue(response.isSuccessful)
        assertEquals("fake-jwt-token", response.body()?.token)
        
        val recordedRequest = server.takeRequest()
        assertEquals("/api/auth/login", recordedRequest.path)
        assertEquals("POST", recordedRequest.method)
    }

    @Test
    fun `register returns success response`() = runTest {
        val mockResponse = MockResponse()
            .setResponseCode(200)
        server.enqueue(mockResponse)

        val response = apiService.register(com.wilove.vaulten.data.remote.model.RegisterRequest("user", "test@test.com", "pass1234"))

        assertTrue(response.isSuccessful)
        
        val recordedRequest = server.takeRequest()
        assertEquals("/api/auth/register", recordedRequest.path)
        assertEquals("POST", recordedRequest.method)
    }
}
