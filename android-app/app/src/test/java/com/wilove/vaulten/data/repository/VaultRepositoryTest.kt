package com.wilove.vaulten.data.repository

import com.wilove.vaulten.data.local.dao.VaultDao
import com.wilove.vaulten.data.local.entity.VaultEntity
import com.wilove.vaulten.data.remote.VaultApiService
import com.wilove.vaulten.data.remote.model.*
import com.wilove.vaulten.domain.model.Credential
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNotNull
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
        val entities = emptyList<VaultEntity>()
        coEvery { vaultDao.getAllCredentials() } returns flowOf(entities)

        val result = repository.getAllCredentials().first()

        assertEquals(0, result.size)
    }

    @Test
    fun `sync success updates local cache`() = runTest {
        val remoteEntries = emptyList<VaultEntryResponse>()
        coEvery { apiService.getEntries() } returns Response.success(remoteEntries)

        repository.sync()

        coVerify { vaultDao.clearAll() }
        coVerify { vaultDao.insertCredentials(any()) }
    }

    // ─── Password save correctness ────────────────────────────────────────────

    @Test
    fun `saveCredential sends the typed password to the API`() = runTest {
        val typedPassword = "MiContraseña123!"
        val requestSlot = slot<VaultEntryRequest>()
        coEvery { apiService.createEntry(capture(requestSlot)) } returns
            Response.success(fakeApiResponse(password = typedPassword))

        repository.saveCredential(fakeCredential(id = "", password = typedPassword))

        assertEquals(
            "El repositorio debe enviar a la API exactamente la contraseña que escribió el usuario",
            typedPassword,
            requestSlot.captured.password
        )
    }

    @Test
    fun `saveCredential stores the typed password in Room even when API returns a different value`() = runTest {
        // Verifica que Room siempre guarda exactamente la contraseña que escribió el usuario,
        // independientemente de lo que devuelva la API en el campo password.
        // Si este test falla, el repositorio está usando la respuesta de la API en lugar de
        // la contraseña original del usuario.
        val typedPassword = "MiContraseña123!"
        val apiReturnedPassword = "ENC:U2FsdGVkX1+bWlDb250cmFzZW5hMTIzIQ==" // valor diferente (ej. cifrado)

        coEvery { apiService.createEntry(any()) } returns
            Response.success(fakeApiResponse(password = apiReturnedPassword))

        val entitySlot = slot<VaultEntity>()
        coEvery { vaultDao.insertCredential(capture(entitySlot)) } answers { Unit }

        repository.saveCredential(fakeCredential(id = "", password = typedPassword))

        assertNotNull("La credencial debe haberse guardado en Room", entitySlot.captured)
        assertEquals(
            "Room debe almacenar la contraseña que escribió el usuario, no la que devolvió la API",
            typedPassword,
            entitySlot.captured.password
        )
        assertNotEquals(
            "Room NO debe almacenar la contraseña que devolvió la API si es diferente a la del usuario",
            apiReturnedPassword,
            entitySlot.captured.password
        )
    }

    // ─── Helpers ──────────────────────────────────────────────────────────────

    private fun fakeApiResponse(
        id: Long = 1L,
        name: String = "Gmail",
        username: String = "user@gmail.com",
        password: String = "password",
        url: String = "https://gmail.com"
    ) = VaultEntryResponse(
        id = id,
        name = name,
        username = username,
        password = password,
        url = url,
        type = VaultType.LOGIN,
        category = "General",
        createdAt = "2024-01-01T00:00:00+00:00",
        updatedAt = "2024-01-01T00:00:00+00:00"
    )

    private fun fakeCredential(
        id: String = "1",
        name: String = "Gmail",
        username: String = "user@gmail.com",
        password: String = "password",
        url: String = "https://gmail.com"
    ) = Credential(id = id, name = name, username = username, password = password, url = url)
}
