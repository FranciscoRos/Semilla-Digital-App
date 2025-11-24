package com.semilladigital.geomap.di

import com.semilladigital.geomap.data.remote.GeomapApi
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
}