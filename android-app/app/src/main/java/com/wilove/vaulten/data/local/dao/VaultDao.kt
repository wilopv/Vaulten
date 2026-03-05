package com.wilove.vaulten.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.wilove.vaulten.data.local.entity.VaultEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface VaultDao {
    @Query("SELECT * FROM vault_entries ORDER BY lastModified DESC")
    fun getAllCredentials(): Flow<List<VaultEntity>>

    @Query("SELECT * FROM vault_entries ORDER BY lastModified DESC LIMIT :limit")
    fun getRecentCredentials(limit: Int): Flow<List<VaultEntity>>

    @Query("SELECT * FROM vault_entries WHERE id = :id")
    suspend fun getCredentialById(id: String): VaultEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCredentials(credentials: List<VaultEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCredential(credential: VaultEntity)

    @Query("DELETE FROM vault_entries WHERE id = :id")
    suspend fun deleteCredential(id: String)

    @Query("DELETE FROM vault_entries")
    suspend fun clearAll()
}
