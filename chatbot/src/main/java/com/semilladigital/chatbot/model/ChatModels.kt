package com.semilladigital.chatbot.model

data class GeminiRequest(
    val prompt: String
)

data class GeminiResponse(
    val respuesta: String
)

data class ChatMessage(
    val text: String,
    val isUser: Boolean,
    val timestamp: Long = System.currentTimeMillis()
)