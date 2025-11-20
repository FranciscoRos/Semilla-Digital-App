package com.semilladigital.auth.data.repository // Asegúrate que este package coincida con tu carpeta

import com.semilladigital.auth.data.remote.AuthApiService
import com.semilladigital.auth.data.remote.dto.LoginRequestDto
import com.semilladigital.auth.data.remote.dto.ParcelaRegistroDto
import com.semilladigital.auth.data.remote.dto.RegisterRequestDto
import com.semilladigital.auth.data.remote.dto.UsuarioRegistroDataDto
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

            if (response.usuario == null) {
                throw Exception("La API no devolvió un objeto de usuario.")
            }

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


    override suspend fun register(registrationData: Map<String, Any>): Result<Unit> {
        return try {
            // Ya recibimos el mapa listo desde el ViewModel
            apiService.register(registrationData)
            Result.success(Unit)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }

// Función de mapeo segura para Login
private fun UserDto.toUser(): User {
    val safeId = this.id ?: throw Exception("El ID del usuario es nulo en la respuesta de la API.")

    return User(
        id = safeId,
        nombre = this.nombre ?: "Usuario Desconocido",
        correo = this.correo ?: "Sin Correo",
        nombreRol = this.rol?.firstOrNull()?.nombreRol ?: "Sin Rol",
        estatus = this.estatus ?: "Desconocido"
    )
}}