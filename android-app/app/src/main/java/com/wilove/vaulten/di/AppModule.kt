package com.wilove.vaulten.di

import android.content.Context
import com.wilove.vaulten.data.local.TokenManager
import com.wilove.vaulten.data.local.VaultDatabase
import com.wilove.vaulten.data.local.dao.VaultDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideTokenManager(@ApplicationContext context: Context): TokenManager =
        TokenManager(context)

    @Provides
    @Singleton
    fun provideVaultDatabase(@ApplicationContext context: Context): VaultDatabase =
        VaultDatabase.getInstance(context)

    @Provides
    fun provideVaultDao(db: VaultDatabase): VaultDao = db.vaultDao()
}
