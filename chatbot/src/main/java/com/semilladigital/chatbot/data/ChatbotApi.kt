package com.semilladigital.chatbot.data

import com.semilladigital.chatbot.model.GeminiRequest
import com.semilladigital.chatbot.model.GeminiResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ChatbotApi {
    @POST("gemini")
    suspend fun sendMessage(@Body request: GeminiRequest): Response<GeminiResponse>
}