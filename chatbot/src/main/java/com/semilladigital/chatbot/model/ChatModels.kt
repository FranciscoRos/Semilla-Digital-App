package com.semilladigital.chatbot.model

// Lo que enviamos al servidor (Laravel)
data class GeminiRequest(
    val prompt: String
)

// Lo que recibimos del servidor
data class GeminiResponse(
    val respuesta: String
)

// Modelo para la UI del chat
data class ChatMessage(
    val text: String,
    val isUser: Boolean,
    val timestamp: Long = System.currentTimeMillis()
)