package com.semilladigital.supports.di

import com.semilladigital.supports.data.remote.ApoyosApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ApoyosNetworkModule {

    @Provides
    @Singleton
    fun provideApoyosApiService(retrofit: Retrofit): ApoyosApiService {
        return retrofit.create(ApoyosApiService::class.java)
    }
}