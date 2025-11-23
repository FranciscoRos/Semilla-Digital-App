package com.semilladigital.chatbot.data

import com.semilladigital.chatbot.model.ChatMessage
import com.semilladigital.chatbot.model.GeminiRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChatRepository @Inject constructor(
    private val api: ChatbotApi
) {
    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val messages = _messages.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    suspend fun sendMessage(prompt: String) {
        if (prompt.isBlank()) return

        val userMsg = ChatMessage(text = prompt, isUser = true)
        _messages.value = _messages.value + userMsg
        _isLoading.value = true

        try {
            val response = api.sendMessage(GeminiRequest(prompt))

            if (response.isSuccessful && response.body() != null) {
                val botMsg = ChatMessage(text = response.body()!!.respuesta, isUser = false)
                _messages.value = _messages.value + botMsg
            } else {
                _messages.value = _messages.value + ChatMessage("Error: ${response.code()}", isUser = false)
            }
        } catch (e: Exception) {
            _messages.value = _messages.value + ChatMessage("Error de conexión", isUser = false)
        } finally {
            _isLoading.value = false
        }
    }
}