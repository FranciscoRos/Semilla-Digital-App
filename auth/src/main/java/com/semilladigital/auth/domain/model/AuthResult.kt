package com.semilladigital.auth.domain.model

data class AuthResult(
    val user: User,
    val token: String
)