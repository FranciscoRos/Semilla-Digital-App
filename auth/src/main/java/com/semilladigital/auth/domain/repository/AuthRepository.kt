package com.semilladigital.auth.domain.repository

import com.semilladigital.auth.domain.model.AuthResult
import com.semilladigital.auth.domain.model.User

interface AuthRepository {
    suspend fun login(correo: String, contrasena: String): Result<AuthResult>

    suspend fun register(registrationData: Map<String, Any>): Result<Unit>
    suspend fun getUserProfile(token: String): Result<User>
}