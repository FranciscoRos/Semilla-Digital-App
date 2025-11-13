package com.semilladigital.auth.domain.model

// Un modelo para la respuesta de autenticación
data class AuthResult(
    val user: User,
    val token: String
)