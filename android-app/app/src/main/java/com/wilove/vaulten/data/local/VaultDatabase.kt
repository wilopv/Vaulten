package com.wilove.vaulten.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.wilove.vaulten.data.local.dao.VaultDao
import com.wilove.vaulten.data.local.entity.VaultEntity

@Database(entities = [VaultEntity::class], version = 1, exportSchema = false)
abstract class VaultDatabase : RoomDatabase() {
    abstract fun vaultDao(): VaultDao

    companion object {
        @Volatile
        private var INSTANCE: VaultDatabase? = null

        fun getInstance(context: android.content.Context): VaultDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = androidx.room.Room.databaseBuilder(
                    context.applicationContext,
                    VaultDatabase::class.java,
                    "vaulten-db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
