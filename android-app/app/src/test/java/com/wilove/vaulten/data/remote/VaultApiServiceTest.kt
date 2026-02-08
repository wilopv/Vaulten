package com.wilove.vaulten.data.remote

import retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.wilove.vaulten.data.remote.model.VaultType
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

class VaultApiServiceTest {

    private lateinit var server: MockWebServer
    private lateinit var apiService: VaultApiService

    private val json = Json { ignoreUnknownKeys = true }
    private val contentType = "application/json".toMediaType()

    @Before
    fun setup() {
        server = MockWebServer()
        apiService = Retrofit.Builder()
            .baseUrl(server.url("/"))
            .addConverterFactory(json.asConverterFactory(contentType))
            .build()
            .create(VaultApiService::class.java)
    }

    @After
    fun tearDown() {
        server.shutdown()
    }

    @Test
    fun `getEntries returns list of entries`() = runTest {
        val mockResponse = MockResponse()
            .setResponseCode(200)
            .setBody("""[{"id": 1, "name": "Gmail", "type": "LOGIN", "category": "Email", "createdAt": "...", "updatedAt": "..."}]""")
        server.enqueue(mockResponse)

        val response = apiService.getEntries()

        assertTrue(response.isSuccessful)
        assertEquals(1, response.body()?.size)
        assertEquals("Gmail", response.body()?.first()?.name)
    }

    @Test
    fun `sync returns updated entries and server time`() = runTest {
        val mockResponse = MockResponse()
            .setResponseCode(200)
            .setBody("""{
                "updatedEntries": [{"id": 2, "name": "Dropbox", "type": "LOGIN", "category": "Work", "createdAt": "...", "updatedAt": "..."}],
                "serverTime": "2024-02-08T16:10:00"
            }""")
        server.enqueue(mockResponse)

        val response = apiService.sync("2024-02-08T10:00:00")

        assertTrue(response.isSuccessful)
        assertEquals(1, response.body()?.updatedEntries?.size)
        assertEquals("2024-02-08T16:10:00", response.body()?.serverTime)
        
        val recordedRequest = server.takeRequest()
        assertTrue(recordedRequest.path!!.startsWith("/api/vault/sync?since="))
    }
}
