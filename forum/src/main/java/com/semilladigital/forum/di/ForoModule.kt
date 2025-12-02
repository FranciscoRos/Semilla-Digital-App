package com.semilladigital.forum.di

import com.semilladigital.forum.data.remote.ForoApiService
import com.semilladigital.forum.data.repository.ForoRepositoryImpl
import com.semilladigital.forum.domain.repository.ForoRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class ForoModule {

    @Binds
    @Singleton
    abstract fun bindForoRepository(
        foroRepositoryImpl: ForoRepositoryImpl
    ): ForoRepository

    companion object {
        @Provides
        @Singleton
        fun provideForoApiService(retrofit: Retrofit): ForoApiService {
            return retrofit.create(ForoApiService::class.java)
        }
    }
}