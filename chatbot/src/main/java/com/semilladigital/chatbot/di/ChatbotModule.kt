package com.semilladigital.chatbot.di

import com.semilladigital.chatbot.data.ChatbotApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ChatbotModule {

    @Provides
    @Singleton
    fun provideChatbotApi(retrofit: Retrofit): ChatbotApi {
        // Usa la instancia base de Retrofit de tu app
        return retrofit.create(ChatbotApi::class.java)
    }
}