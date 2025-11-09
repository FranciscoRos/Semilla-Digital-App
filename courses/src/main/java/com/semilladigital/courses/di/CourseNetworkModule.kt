package com.semilladigital.courses.di

import com.semilladigital.courses.data.remote.CourseApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object CourseNetworkModule {

    @Provides
    @Singleton
    fun provideCourseApiService(retrofit: Retrofit): CourseApiService {
        // Hilt inyecta 'Retrofit' singleton en :core-data
        // y lo usa para crear nuestra interfaz de servicio
        return retrofit.create(CourseApiService::class.java)
    }
}