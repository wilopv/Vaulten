package com.wilove.vaulten.di

import com.wilove.vaulten.data.repository.AuthRepositoryImpl
import com.wilove.vaulten.data.repository.VaultRepositoryImpl
import com.wilove.vaulten.domain.repository.AuthRepository
import com.wilove.vaulten.domain.repository.VaultRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindAuthRepository(impl: AuthRepositoryImpl): AuthRepository

    @Binds
    @Singleton
    abstract fun bindVaultRepository(impl: VaultRepositoryImpl): VaultRepository
}
