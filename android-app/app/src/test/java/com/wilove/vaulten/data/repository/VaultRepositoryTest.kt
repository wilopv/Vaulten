package com.wilove.vaulten.data.repository

import com.wilove.vaulten.data.remote.VaultApiService
import com.wilove.vaulten.data.remote.model.*
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import retrofit2.Response

class VaultRepositoryTest {

    private lateinit var apiService: VaultApiService
    private lateinit var repository: VaultRepository

    @Before
    fun setup() {
        apiService = mockk()
        repository = VaultRepository(apiService)
    }

    @Test
    fun `getEntries success returns list`() = runTest {
        val entries = listOf(mockk<VaultEntryResponse>(relaxed = true))
        coEvery { apiService.getEntries() } returns Response.success(entries)

        val result = repository.getEntries()

        assertTrue(result.isSuccess)
        assertEquals(entries, result.getOrNull())
    }

    @Test
    fun `sync success returns SyncResponse`() = runTest {
        val syncResponse = SyncResponse(emptyList(), "2024-02-08T16:10:00")
        coEvery { apiService.sync(any()) } returns Response.success(syncResponse)

        val result = repository.sync("2024-02-08T10:00:00")

        assertTrue(result.isSuccess)
        assertEquals(syncResponse, result.getOrNull())
    }
}
