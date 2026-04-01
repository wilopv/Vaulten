package com.wilove.vaulten.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.wilove.vaulten.data.local.entity.VaultEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface VaultDao {
    @Query("SELECT * FROM vault_entries WHERE deletedAt IS NULL ORDER BY lastModified DESC")
    fun getAllCredentials(): Flow<List<VaultEntity>>

    @Query("SELECT * FROM vault_entries WHERE deletedAt IS NULL ORDER BY lastModified DESC LIMIT :limit")
    fun getRecentCredentials(limit: Int): Flow<List<VaultEntity>>

    @Query("SELECT * FROM vault_entries WHERE deletedAt IS NOT NULL ORDER BY deletedAt DESC")
    fun getDeletedCredentials(): Flow<List<VaultEntity>>

    @Query("SELECT * FROM vault_entries WHERE id = :id")
    suspend fun getCredentialById(id: String): VaultEntity?

    @Query("SELECT * FROM vault_entries WHERE url LIKE '%' || :query || '%' OR name LIKE '%' || :query || '%'")
    suspend fun searchCredentials(query: String): List<VaultEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCredentials(credentials: List<VaultEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCredential(credential: VaultEntity)

    @Query("UPDATE vault_entries SET deletedAt = :deletedAt WHERE id = :id")
    suspend fun softDeleteCredential(id: String, deletedAt: Long)

    @Query("UPDATE vault_entries SET deletedAt = NULL WHERE id = :id")
    suspend fun restoreCredential(id: String)

    @Query("DELETE FROM vault_entries WHERE id = :id")
    suspend fun permanentlyDeleteCredential(id: String)

    @Query("DELETE FROM vault_entries WHERE deletedAt IS NULL")
    suspend fun clearAll()
}
