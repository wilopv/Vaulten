package com.wilove.vaulten.data.repository

import com.wilove.vaulten.data.local.dao.VaultDao
import com.wilove.vaulten.data.local.mapper.toDomain
import com.wilove.vaulten.data.local.mapper.toDomainList
import com.wilove.vaulten.data.local.mapper.toEntity
import com.wilove.vaulten.data.remote.VaultApiService
import com.wilove.vaulten.data.remote.mapper.toDomain
import com.wilove.vaulten.data.remote.model.VaultEntryRequest
import com.wilove.vaulten.data.remote.model.VaultType
import com.wilove.vaulten.domain.model.Credential
import com.wilove.vaulten.domain.model.SecurityAlert
import com.wilove.vaulten.domain.repository.VaultRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import retrofit2.Response
import java.io.IOException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.inject.Inject

/**
 * Repository responsible for managing vault entries and synchronization.
 */
class VaultRepositoryImpl @Inject constructor(
    private val apiService: VaultApiService,
    private val vaultDao: VaultDao
) : VaultRepository {

    override fun getRecentCredentials(limit: Int): Flow<List<Credential>> {
        return vaultDao.getRecentCredentials(limit).map { it.toDomainList() }
    }

    override fun getAllCredentials(): Flow<List<Credential>> {
        return vaultDao.getAllCredentials().map { it.toDomainList() }
    }

    override suspend fun getSecurityAlerts(): List<SecurityAlert> {
        return emptyList()
    }

    override suspend fun getCredentialById(id: String): Credential? {
        return vaultDao.getCredentialById(id)?.toDomain()
    }

    override suspend fun saveCredential(credential: Credential) {
        val idLong = credential.id.toLongOrNull()
        val request = VaultEntryRequest(
            name = credential.name,
            username = credential.username,
            password = credential.password,
            url = credential.url,
            androidPackageName = credential.androidPackageName,
            type = VaultType.LOGIN,
            category = "General"
        )

        val result = if (idLong == null || idLong == 0L) {
            handleApiCall(::saveError) { apiService.createEntry(request) }
        } else {
            handleApiCall(::saveError) { apiService.updateEntry(idLong, request) }
        }

        if (result.isSuccess) {
            val remoteEntry = result.getOrNull()
            if (remoteEntry != null) {
                val serverAssignedId = remoteEntry.id.toString()
                vaultDao.insertCredential(credential.copy(id = serverAssignedId).toEntity())
            }
        } else {
            throw result.exceptionOrNull() ?: Exception("No se pudo guardar la credencial.")
        }
    }

    override suspend fun deleteCredential(id: String) {
        vaultDao.softDeleteCredential(id, System.currentTimeMillis())
    }

    override fun getDeletedCredentials(): Flow<List<Credential>> {
        return vaultDao.getDeletedCredentials().map { it.toDomainList() }
    }

    override suspend fun restoreCredential(id: String) {
        vaultDao.restoreCredential(id)
    }

    override suspend fun permanentlyDeleteCredential(id: String) {
        val idLong = id.toLongOrNull()
        if (idLong != null) {
            val response = try {
                apiService.deleteEntry(idLong)
            } catch (e: Exception) {
                throw networkError(e)
            }
            // 204 = deleted now, 404 = already gone (deleted from another device) — both are fine.
            if (!response.isSuccessful && response.code() != 404) {
                throw deleteError(response.code())
            }
        }
        vaultDao.permanentlyDeleteCredential(id)
    }

    override suspend fun sync() {
        val result = handleApiCall(::syncError) { apiService.getEntries() }
        if (result.isSuccess) {
            val remoteEntries = result.getOrNull() ?: emptyList()
            vaultDao.clearAll()
            vaultDao.insertCredentials(remoteEntries.map { it.toDomain().toEntity() })
        }
    }

    // ── Helpers ──────────────────────────────────────────────────────────────

    private suspend fun <T> handleApiCall(
        errorMapper: (Int) -> Exception,
        call: suspend () -> Response<T>
    ): Result<T> {
        return try {
            val response = call()
            when {
                response.isSuccessful && response.body() != null ->
                    Result.success(response.body()!!)
                response.isSuccessful && response.code() == 204 -> {
                    @Suppress("UNCHECKED_CAST")
                    Result.success(Unit as T)
                }
                else -> Result.failure(errorMapper(response.code()))
            }
        } catch (e: Exception) {
            Result.failure(networkError(e))
        }
    }

    private fun saveError(code: Int): Exception = when (code) {
        400  -> Exception("Los datos de la credencial no son válidos.")
        401  -> Exception("Tu sesión ha expirado. Vuelve a iniciar sesión.")
        403  -> Exception("No tienes permiso para realizar esta acción.")
        409  -> Exception("Ya existe una credencial con ese nombre.")
        422  -> Exception("El formato de los datos no es válido.")
        in 500..599 -> Exception("El servidor no está disponible ahora mismo. Inténtalo más tarde.")
        else -> Exception("No se pudo guardar la credencial. Inténtalo de nuevo.")
    }

    private fun deleteError(code: Int): Exception = when (code) {
        401  -> Exception("Tu sesión ha expirado. Vuelve a iniciar sesión.")
        403  -> Exception("No tienes permiso para eliminar esta credencial.")
        in 500..599 -> Exception("El servidor no está disponible ahora mismo. Inténtalo más tarde.")
        else -> Exception("No se pudo eliminar la credencial. Inténtalo de nuevo.")
    }

    private fun syncError(code: Int): Exception = when (code) {
        401  -> Exception("Tu sesión ha expirado. Vuelve a iniciar sesión.")
        in 500..599 -> Exception("El servidor no está disponible ahora mismo. Inténtalo más tarde.")
        else -> Exception("No se pudo sincronizar la bóveda. Inténtalo de nuevo.")
    }

    private fun networkError(e: Exception): Exception = when (e) {
        is UnknownHostException,
        is ConnectException       -> Exception("Sin conexión a internet. Comprueba tu red e inténtalo de nuevo.")
        is SocketTimeoutException -> Exception("El servidor tardó demasiado en responder. Inténtalo de nuevo.")
        is IOException            -> Exception("Error de conexión. Inténtalo de nuevo.")
        else                      -> Exception("Ocurrió un error inesperado. Inténtalo de nuevo.")
    }
}
