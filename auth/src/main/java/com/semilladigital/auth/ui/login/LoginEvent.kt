package com.semilladigital.auth.ui.login

// Acciones que el usuario puede disparar desde la UI
sealed class LoginEvent {
    data class OnEmailChanged(val email: String) : LoginEvent()
    data class OnPasswordChanged(val contrasena: String) : LoginEvent()
    object OnLoginClick : LoginEvent()
}