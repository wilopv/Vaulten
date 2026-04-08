package com.wilove.vaulten.data.local

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

/**
 * Manages the secure storage of the JWT token using EncryptedSharedPreferences.
 */
class TokenManager(context: Context) {

    // Initialize the master key for encryption
    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    // Create the secure shared preferences instance
    private val prefs = EncryptedSharedPreferences.create(
        context,
        "vaulten_secure_prefs",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    /**
     * Saves the JWT token to secure storage.
     */
    fun saveToken(token: String) {
        prefs.edit().putString(KEY_TOKEN, token).apply()
    }

    /**
     * Retrieves the stored JWT token, or null if it doesn't exist.
     */
    fun getToken(): String? {
        return prefs.getString(KEY_TOKEN, null)
    }

    /**
     * Deletes the stored JWT token from secure storage.
     */
    fun deleteToken() {
        prefs.edit().remove(KEY_TOKEN).apply()
    }

    /**
     * Saves the timestamp (epoch ms) of the last user activity.
     * Called when the app goes to background so the lock screen can decide
     * whether to require re-authentication on next launch.
     */
    fun saveLastActiveTimestamp(timestampMs: Long) {
        prefs.edit().putLong(KEY_LAST_ACTIVE, timestampMs).apply()
    }

    /**
     * Returns the last saved activity timestamp, or 0 if never set.
     */
    fun getLastActiveTimestamp(): Long {
        return prefs.getLong(KEY_LAST_ACTIVE, 0L)
    }

    /**
     * Saves the email and password for biometric login.
     * Stored in the same AES-256-GCM encrypted prefs as the token.
     */
    fun saveBiometricCredentials(email: String, password: String) {
        prefs.edit()
            .putString(KEY_BIOMETRIC_EMAIL, email)
            .putString(KEY_BIOMETRIC_PASSWORD, password)
            .apply()
    }

    fun getBiometricEmail(): String? = prefs.getString(KEY_BIOMETRIC_EMAIL, null)
    fun getBiometricPassword(): String? = prefs.getString(KEY_BIOMETRIC_PASSWORD, null)
    fun hasBiometricCredentials(): Boolean = getBiometricEmail() != null

    fun clearBiometricCredentials() {
        prefs.edit()
            .remove(KEY_BIOMETRIC_EMAIL)
            .remove(KEY_BIOMETRIC_PASSWORD)
            .apply()
    }

    fun setBiometricLockEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_BIOMETRIC_LOCK_ENABLED, enabled).apply()
    }

    fun isBiometricLockEnabled(): Boolean {
        return prefs.getBoolean(KEY_BIOMETRIC_LOCK_ENABLED, false)
    }

    companion object {
        private const val KEY_TOKEN = "jwt_token"
        private const val KEY_LAST_ACTIVE = "last_active_timestamp"
        private const val KEY_BIOMETRIC_EMAIL = "biometric_email"
        private const val KEY_BIOMETRIC_PASSWORD = "biometric_password"
        private const val KEY_BIOMETRIC_LOCK_ENABLED = "biometric_lock_enabled"
    }
}
