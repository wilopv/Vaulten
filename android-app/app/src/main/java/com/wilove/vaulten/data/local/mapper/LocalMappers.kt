package com.wilove.vaulten.data.local.mapper

import com.wilove.vaulten.data.local.entity.VaultEntity
import com.wilove.vaulten.domain.model.Credential

fun VaultEntity.toDomain(): Credential {
    return Credential(
        id = id,
        name = name,
        username = username,
        password = password,
        url = url,
        lastModified = lastModified
    )
}

fun Credential.toEntity(): VaultEntity {
    return VaultEntity(
        id = id,
        name = name,
        username = username,
        password = password,
        url = url,
        lastModified = lastModified
    )
}

fun List<VaultEntity>.toDomainList(): List<Credential> {
    return map { it.toDomain() }
}
