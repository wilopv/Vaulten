package com.wilove.vaulten.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.wilove.vaulten.data.local.dao.VaultDao
import com.wilove.vaulten.data.local.entity.VaultEntity

@Database(entities = [VaultEntity::class], version = 1, exportSchema = false)
abstract class VaultDatabase : RoomDatabase() {
    abstract fun vaultDao(): VaultDao
}
