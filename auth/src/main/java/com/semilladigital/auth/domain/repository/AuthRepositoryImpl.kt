package com.semilladigital.auth.data.repository

import com.semilladigital.auth.data.remote.AuthApiService
import com.semilladigital.auth.data.remote.dto.LoginRequestDto
import com.semilladigital.auth.data.remote.dto.UserDto
import com.semilladigital.auth.domain.model.AuthResult
import com.semilladigital.auth.domain.model.User
import com.semilladigital.auth.domain.repository.AuthRepository
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val apiService: AuthApiService
) : AuthRepository {

    override suspend fun login(correo: String, contrasena: String): Result<AuthResult> {
        return try {
            val requestDto = LoginRequestDto(
                correo = correo,
                contrasena = contrasena
            )

            val response = apiService.login(requestDto)

            val authResult = AuthResult(
                user = response.usuario.toUser(),
                token = response.token
            )

            Result.success(authResult)

        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }
}

// --- ¡CAMBIO AQUÍ! ---
// Actualizamos la lógica para leer el primer rol de la lista
private fun UserDto.toUser(): User {
    return User(
        id = this.id,
        nombre = this.nombre,
        correo = this.correo,
        // Usamos .firstOrNull() para tomar el primer rol de la lista
        // Si la lista es nula o está vacía, devuelve "Sin Rol"
        nombreRol = this.rol?.firstOrNull()?.nombreRol ?: "Sin Rol",
        estatus = this.estatus ?: "Desconocido"
    )
}