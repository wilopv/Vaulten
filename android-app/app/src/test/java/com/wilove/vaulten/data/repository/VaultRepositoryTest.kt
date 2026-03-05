package com.wilove.vaulten.data.repository

import com.wilove.vaulten.data.local.dao.VaultDao
import com.wilove.vaulten.data.remote.VaultApiService
import com.wilove.vaulten.data.remote.model.*
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import retrofit2.Response

class VaultRepositoryTest {

    private lateinit var apiService: VaultApiService
    private lateinit var vaultDao: VaultDao
    private lateinit var repository: VaultRepositoryImpl

    @Before
    fun setup() {
        apiService = mockk()
        vaultDao = mockk(relaxed = true)
        repository = VaultRepositoryImpl(apiService, vaultDao)
    }

    @Test
    fun `getAllCredentials returns flow from dao`() = runTest {
        val entities = emptyList<com.wilove.vaulten.data.local.VaultEntity>()
        coEvery { vaultDao.getAllCredentials() } returns flowOf(entities)

        val result = repository.getAllCredentials().first()

        assertEquals(0, result.size)
    }

    @Test
    fun `sync success updates local cache`() = runTest {
        val remoteEntries = emptyList<VaultEntryResponse>()
        coEvery { apiService.getEntries() } returns Response.success(remoteEntries)

        repository.sync()

        io.mockk.verify { vaultDao.clearAll() }
        io.mockk.verify { vaultDao.insertCredentials(any()) }
    }
}
