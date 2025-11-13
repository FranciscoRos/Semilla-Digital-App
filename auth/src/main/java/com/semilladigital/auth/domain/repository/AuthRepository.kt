package com.semilladigital.auth.domain.repository

import com.semilladigital.auth.domain.model.AuthResult

interface AuthRepository {

    // Función de Login
    suspend fun login(correo: String, contrasena: String): Result<AuthResult>

    // Aquí pondríamos la función de registro después
    // suspend fun register(...)
}