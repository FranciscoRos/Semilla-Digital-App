package com.semilladigital.chatbot.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.semilladigital.chatbot.data.ChatRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val repository: ChatRepository
) : ViewModel() {

    val messages = repository.messages
    val isLoading = repository.isLoading

    fun sendMessage(prompt: String) {
        viewModelScope.launch {
            repository.sendMessage(prompt)
        }
    }

    fun setContext(context: String) {
        repository.setContext(context)
    }
}