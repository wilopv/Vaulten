package com.wilove.vaulten.data.local

/**
 * Determines whether the app session has timed out and re-authentication is required.
 * Pure utility — no Android dependencies, easy to unit-test.
 */
object SessionManager {

    /** Lock the app after 5 minutes in the background. */
    private const val LOCK_TIMEOUT_MS = 5 * 60 * 1000L

    /**
     * Returns true if [lastActiveTimestampMs] is non-zero and the elapsed time
     * since that moment exceeds [LOCK_TIMEOUT_MS].
     *
     * Returns false if the timestamp is 0 (first run / never saved), so the user
     * is not prompted to unlock before they ever log in.
     */
    fun isLockRequired(lastActiveTimestampMs: Long): Boolean {
        if (lastActiveTimestampMs == 0L) return false
        return System.currentTimeMillis() - lastActiveTimestampMs > LOCK_TIMEOUT_MS
    }
}
