package com.semilladigital.auth.domain.repository

import com.semilladigital.auth.domain.model.AuthResult

interface AuthRepository {
    suspend fun login(correo: String, contrasena: String): Result<AuthResult>

    // --- NUEVO: Función para registro ---
    suspend fun register(
        nombreCompleto: String,
        correo: String,
        contrasena: String,
        curp: String,
        localidad: String,
        tipoCultivo: String
    ): Result<Unit>
}