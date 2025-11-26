package com.semilladigital.supports.di

import com.semilladigital.supports.data.repository.ApoyosRepositoryImpl
import com.semilladigital.supports.domain.repository.ApoyosRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class ApoyosRepositoryModule {

    @Binds
    @Singleton
    abstract fun bindApoyosRepository(
        impl: ApoyosRepositoryImpl
    ): ApoyosRepository
}