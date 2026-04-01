package com.wilove.vaulten.domain.model

data class CredentialFilter(
    val domain: String? = null,
    val modifiedAfter: Long? = null,
    val modifiedBefore: Long? = null
)
