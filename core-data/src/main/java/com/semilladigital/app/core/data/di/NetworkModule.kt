package com.semilladigital.app.core.data.di

// En core-data/src/main/kotlin/com/semilladigital/core_data/NetworkModule.kt

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    // 1. Define la URL base de tu API de Laravel
    private const val BASE_URL = "http:192.168.1.67:8000/api/" // <-- IP LOCAL

    // 2. Provee un interceptor de logs (para ver las llamadas en el Logcat)
    @Provides
    @Singleton
    fun provideLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY // Nivel de log: BODY ve todo
        }
    }

    // 3. Provee el cliente OkHttpClient
    @Provides
    @Singleton
    fun provideOkHttpClient(loggingInterceptor: HttpLoggingInterceptor): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor) // Añade el interceptor de logs
            .build()
    }

    // 4. Provee la instancia principal de Retrofit
    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create()) // Usa Gson
            .build()
    }
}