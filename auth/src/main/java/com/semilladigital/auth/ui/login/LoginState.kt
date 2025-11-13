package com.semilladigital.auth.ui.login

// Define todos los elementos de la UI
data class LoginState(
    val email: String = "",
    val contrasena: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val loginSuccess: Boolean = false // Para saber cuándo navegar
)