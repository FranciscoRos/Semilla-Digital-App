package com.semilladigital.chatbot.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.semilladigital.chatbot.data.ChatbotApi
import com.semilladigital.chatbot.model.ChatMessage
import com.semilladigital.chatbot.model.GeminiRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val api: ChatbotApi
) : ViewModel() {

    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val messages = _messages.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    fun sendMessage(prompt: String) {
        if (prompt.isBlank()) return

        val userMsg = ChatMessage(text = prompt, isUser = true)
        _messages.value = _messages.value + userMsg
        _isLoading.value = true

        viewModelScope.launch {
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
}