package com.wilove.vaulten.data.repository

import com.wilove.vaulten.data.remote.AuthApiService
import com.wilove.vaulten.data.remote.model.ChangePasswordRequest
import com.wilove.vaulten.data.remote.model.LoginRequest
import com.wilove.vaulten.data.remote.model.RegisterRequest
import com.wilove.vaulten.data.local.TokenManager
import com.wilove.vaulten.domain.repository.AuthRepository
import java.io.IOException
import javax.inject.Inject
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

/**
 * Repository handling authentication logic and token lifecycle.
 * Implements the domain [AuthRepository] interface.
 */
class AuthRepositoryImpl @Inject constructor(
    private val authApiService: AuthApiService,
    private val tokenManager: TokenManager
) : AuthRepository {

    override suspend fun register(username: String, email: String, password: String): Result<Unit> {
        return try {
            val response = authApiService.register(RegisterRequest(username, email, password))
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(registerError(response.code()))
            }
        } catch (e: Exception) {
            Result.failure(networkError(e))
        }
    }

    override suspend fun login(username: String, password: String): Result<String> {
        return try {
            val response = authApiService.login(LoginRequest(username, password))
            if (response.isSuccessful && response.body() != null) {
                val token = response.body()!!.token
                tokenManager.saveToken(token)
                Result.success(token)
            } else {
                Result.failure(loginError(response.code()))
            }
        } catch (e: Exception) {
            Result.failure(networkError(e))
        }
    }

    override fun logout() {
        tokenManager.deleteToken()
    }

    override fun getLoggedToken(): String? = tokenManager.getToken()

    override suspend fun changePassword(currentPassword: String, newPassword: String): Result<Unit> {
        return try {
            val response = authApiService.changePassword(
                ChangePasswordRequest(currentPassword, newPassword)
            )
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(changePasswordError(response.code()))
            }
        } catch (e: Exception) {
            Result.failure(networkError(e))
        }
    }

    // ── Error mappers ────────────────────────────────────────────────────────

    private fun loginError(code: Int): Exception = when (code) {
        400  -> Exception("Los datos introducidos no son válidos.")
        401  -> Exception("El email o la contraseña son incorrectos.")
        403  -> Exception("Tu cuenta no tiene acceso. Contacta con soporte.")
        404  -> Exception("No existe ninguna cuenta con ese email.")
        422  -> Exception("El formato del email o la contraseña no es válido.")
        429  -> Exception("Demasiados intentos fallidos. Espera unos minutos e inténtalo de nuevo.")
        in 500..599 -> Exception("El servidor no está disponible ahora mismo. Inténtalo más tarde.")
        else -> Exception("No se pudo iniciar sesión. Inténtalo de nuevo.")
    }

    private fun registerError(code: Int): Exception = when (code) {
        400  -> Exception("Los datos de registro no son válidos.")
        409  -> Exception("Ya existe una cuenta con ese email.")
        422  -> Exception("El formato del email o la contraseña no es válido.")
        429  -> Exception("Demasiados intentos. Espera unos minutos e inténtalo de nuevo.")
        in 500..599 -> Exception("El servidor no está disponible ahora mismo. Inténtalo más tarde.")
        else -> Exception("No se pudo crear la cuenta. Inténtalo de nuevo.")
    }

    private fun changePasswordError(code: Int): Exception = when (code) {
        400  -> Exception("La solicitud no es válida. Comprueba los datos introducidos.")
        401  -> Exception("La contraseña actual es incorrecta.")
        422  -> Exception("La nueva contraseña no cumple los requisitos de seguridad.")
        in 500..599 -> Exception("El servidor no está disponible ahora mismo. Inténtalo más tarde.")
        else -> Exception("No se pudo cambiar la contraseña. Inténtalo de nuevo.")
    }

    private fun networkError(e: Exception): Exception = when (e) {
        is UnknownHostException,
        is ConnectException  -> Exception("Sin conexión a internet. Comprueba tu red e inténtalo de nuevo.")
        is SocketTimeoutException -> Exception("El servidor tardó demasiado en responder. Inténtalo de nuevo.")
        is IOException       -> Exception("Error de conexión. Inténtalo de nuevo.")
        else                 -> Exception("Ocurrió un error inesperado. Inténtalo de nuevo.")
    }
}
