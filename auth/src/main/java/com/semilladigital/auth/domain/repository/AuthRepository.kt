package com.semilladigital.auth.domain.repository

import com.semilladigital.auth.domain.model.AuthResult

interface AuthRepository {
    suspend fun login(correo: String, contrasena: String): Result<AuthResult>

    suspend fun register(registrationData: Map<String, Any>): Result<Unit>
}