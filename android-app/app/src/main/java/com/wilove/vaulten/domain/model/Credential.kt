package com.wilove.vaulten.domain.model

/**
 * Domain model representing a stored credential (password, login, etc.).
 *
 * @property id Unique identifier for this credential
 * @property name Display name (e.g., "Gmail", "GitHub")
 * @property username Username or email associated with this credential
 * @property password The encrypted password (in real implementation)
 * @property url Associated website or app URL
 * @property lastModified Timestamp of last modification
 */
data class Credential(
    val id: String,
    val name: String,
    val username: String,
    val password: String,
    val url: String? = null,
    val lastModified: Long = System.currentTimeMillis()
)
