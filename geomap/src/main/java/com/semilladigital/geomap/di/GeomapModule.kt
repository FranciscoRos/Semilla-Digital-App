package com.semilladigital.geomap.di

import com.semilladigital.geomap.data.remote.GeomapApi
import com.semilladigital.geomap.data.repository.GeomapRepositoryImpl
import com.semilladigital.geomap.domain.repository.GeomapRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object GeomapModule {

    @Provides
    @Singleton
    fun provideGeomapApi(retrofit: Retrofit): GeomapApi {
        return retrofit.create(GeomapApi::class.java)
    }

    @Provides
    @Singleton
    fun provideGeomapRepository(api: GeomapApi): GeomapRepository {
        return GeomapRepositoryImpl(api)
    }
}