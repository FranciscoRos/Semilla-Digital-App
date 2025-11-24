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

    // Variable para guardar el contexto actual (invisible para el usuario)
    private var currentContext: String = ""

    fun setContext(context: String) {
        currentContext = context
    }

    suspend fun sendMessage(prompt: String) {
        if (prompt.isBlank()) return

        // 1. En la UI mostramos solo lo que el usuario escribió
        val userMsg = ChatMessage(text = prompt, isUser = true)
        _messages.value = _messages.value + userMsg
        _isLoading.value = true

        try {
            // 2. Preparamos el prompt REAL con el contexto inyectado
            val promptToSend = if (currentContext.isNotBlank()) {
                "[CONTEXTO DEL SISTEMA: $currentContext]\n\nPREGUNTA DEL USUARIO: $prompt"
            } else {
                prompt
            }

            // 3. Enviamos el prompt enriquecido a la API
            val response = api.sendMessage(GeminiRequest(promptToSend))

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