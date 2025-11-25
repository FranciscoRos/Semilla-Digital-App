package com.semilladigital.chatbot.data

import com.semilladigital.app.core.data.storage.SessionStorage
import com.semilladigital.chatbot.model.ChatMessage
import com.semilladigital.chatbot.model.GeminiRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChatRepository @Inject constructor(
    private val api: ChatbotApi,
    private val sessionStorage: SessionStorage
) {
    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val messages = _messages.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private var currentContext: String = ""

    fun setContext(context: String) {
        currentContext = context
    }

    suspend fun sendMessage(prompt: String) {
        if (prompt.isBlank()) return

        val userMsg = ChatMessage(text = prompt, isUser = true)
        _messages.value = _messages.value + userMsg
        _isLoading.value = true

        try {
            val actividades = sessionStorage.getActividades().joinToString(", ")
            val userProfileBlock = """
                [PERFIL DEL USUARIO - CONTEXTO PERMANENTE]
                - ID: ${sessionStorage.getUserId()}
                - Nombre: ${sessionStorage.getNombreCompleto()}
                - Email: ${sessionStorage.getEmail()}
                - Rol: ${sessionStorage.getRol()}
                - Estatus: ${sessionStorage.getEstatus()}
                - Actividades Productivas: $actividades
            """.trimIndent()


            val screenContextBlock = if (currentContext.isNotBlank()) {
                "\n[CONTEXTO VISUAL / PANTALLA ACTUAL]\n$currentContext"
            } else {
                ""
            }


            val finalPrompt = """
                $userProfileBlock
                $screenContextBlock
                
                ---------------------------------------------------
                PREGUNTA O COMENTARIO DEL USUARIO:
                $prompt
            """.trimIndent()

            val response = api.sendMessage(GeminiRequest(prompt = finalPrompt))

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