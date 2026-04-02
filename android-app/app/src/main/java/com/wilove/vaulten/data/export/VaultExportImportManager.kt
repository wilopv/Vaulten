package com.wilove.vaulten.data.export

import android.util.Base64
import com.wilove.vaulten.domain.model.Credential
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.security.SecureRandom
import java.security.spec.KeySpec
import javax.crypto.Cipher
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec

@Serializable
data class ExportEntry(
    val name: String,
    val username: String,
    val password: String,
    val url: String? = null,
    val androidPackageName: String? = null
)

@Serializable
private data class EncryptedExportFile(
    val version: Int = 1,
    val data: String  // Base64([salt 16B][iv 12B][AES-GCM ciphertext])
)

/**
 * Handles all export/import serialization and AES-GCM encryption for vault backups.
 *
 * Encrypted format: JSON wrapper containing Base64([salt 16B][iv 12B][AES-GCM ciphertext]).
 * Key derivation: PBKDF2WithHmacSHA256, 100 000 iterations, 256-bit key.
 * The key is never stored — it is re-derived from the password on every decrypt.
 */
object VaultExportImportManager {

    private const val PBKDF2_ITERATIONS = 100_000
    private const val KEY_LENGTH_BITS = 256
    private const val SALT_SIZE = 16
    private const val IV_SIZE = 12
    private const val GCM_TAG_BITS = 128

    private val json = Json { ignoreUnknownKeys = true; encodeDefaults = false }

    // ── CSV ──────────────────────────────────────────────────────────────────

    fun toCsv(credentials: List<Credential>): String {
        val sb = StringBuilder()
        sb.appendLine("name,username,password,url,androidPackageName")
        credentials.forEach { c ->
            sb.appendLine(
                "${csvEscape(c.name)}," +
                "${csvEscape(c.username)}," +
                "${csvEscape(c.password)}," +
                "${csvEscape(c.url ?: "")}," +
                csvEscape(c.androidPackageName ?: "")
            )
        }
        return sb.toString()
    }

    fun parseCsv(content: String): List<ExportEntry> {
        val lines = content.lines().filter { it.isNotBlank() }
        if (lines.size < 2) return emptyList()
        return lines.drop(1).mapNotNull { line ->
            val parts = parseCsvLine(line)
            if (parts.size < 3) null
            else ExportEntry(
                name = parts[0],
                username = parts[1],
                password = parts[2],
                url = parts.getOrNull(3)?.takeIf { it.isNotEmpty() },
                androidPackageName = parts.getOrNull(4)?.takeIf { it.isNotEmpty() }
            )
        }
    }

    private fun csvEscape(value: String): String =
        if (value.contains(',') || value.contains('"') || value.contains('\n')) {
            "\"${value.replace("\"", "\"\"")}\""
        } else value

    private fun parseCsvLine(line: String): List<String> {
        val result = mutableListOf<String>()
        val current = StringBuilder()
        var inQuotes = false
        var i = 0
        while (i < line.length) {
            when {
                line[i] == '"' && inQuotes && i + 1 < line.length && line[i + 1] == '"' -> {
                    current.append('"'); i++
                }
                line[i] == '"' -> inQuotes = !inQuotes
                line[i] == ',' && !inQuotes -> { result.add(current.toString()); current.clear() }
                else -> current.append(line[i])
            }
            i++
        }
        result.add(current.toString())
        return result
    }

    // ── Encrypted JSON ───────────────────────────────────────────────────────

    fun encryptToJson(credentials: List<Credential>, password: String): String {
        val entries = credentials.map {
            ExportEntry(it.name, it.username, it.password, it.url, it.androidPackageName)
        }
        val plaintext = json.encodeToString(entries).toByteArray(Charsets.UTF_8)

        val salt = ByteArray(SALT_SIZE).also { SecureRandom().nextBytes(it) }
        val iv = ByteArray(IV_SIZE).also { SecureRandom().nextBytes(it) }
        val key = deriveKey(password, salt)

        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(Cipher.ENCRYPT_MODE, key, GCMParameterSpec(GCM_TAG_BITS, iv))
        val ciphertext = cipher.doFinal(plaintext)

        val payload = salt + iv + ciphertext
        val base64 = Base64.encodeToString(payload, Base64.NO_WRAP)
        return json.encodeToString(EncryptedExportFile(data = base64))
    }

    /**
     * Decrypts a file produced by [encryptToJson].
     * @throws IllegalArgumentException if the password is wrong or the file is corrupt.
     */
    fun decryptFromJson(fileContent: String, password: String): List<ExportEntry> {
        val exportFile = try {
            json.decodeFromString<EncryptedExportFile>(fileContent)
        } catch (e: Exception) {
            throw IllegalArgumentException("Formato de archivo inválido")
        }

        val payload = Base64.decode(exportFile.data, Base64.NO_WRAP)
        if (payload.size < SALT_SIZE + IV_SIZE) throw IllegalArgumentException("Archivo corrupto")

        val salt = payload.copyOfRange(0, SALT_SIZE)
        val iv = payload.copyOfRange(SALT_SIZE, SALT_SIZE + IV_SIZE)
        val ciphertext = payload.copyOfRange(SALT_SIZE + IV_SIZE, payload.size)

        val key = deriveKey(password, salt)
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(Cipher.DECRYPT_MODE, key, GCMParameterSpec(GCM_TAG_BITS, iv))

        val plaintext = try {
            cipher.doFinal(ciphertext)
        } catch (e: Exception) {
            throw IllegalArgumentException("Contraseña incorrecta o archivo corrupto")
        }

        return json.decodeFromString<List<ExportEntry>>(String(plaintext, Charsets.UTF_8))
    }

    private fun deriveKey(password: String, salt: ByteArray): SecretKeySpec {
        val factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
        val spec: KeySpec = PBEKeySpec(password.toCharArray(), salt, PBKDF2_ITERATIONS, KEY_LENGTH_BITS)
        return SecretKeySpec(factory.generateSecret(spec).encoded, "AES")
    }
}
