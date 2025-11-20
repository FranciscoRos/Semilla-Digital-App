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

    // --- IMPLEMENTACIÓN DEL REGISTRO ---
    override suspend fun register(
        nombreCompleto: String,
        correo: String,
        contrasena: String,
        curp: String,
        localidad: String,
        tipoCultivo: String
    ): Result<Unit> {
        return try {
            // Separamos el nombre simple (esto se puede mejorar luego)
            val partesNombre = nombreCompleto.trim().split(" ")
            val nombreReal = partesNombre.firstOrNull() ?: ""
            val apellido1Real = partesNombre.drop(1).firstOrNull() ?: ""

            // Datos "Dummy" para la parcela (Coordenadas de Chetumal/Bacalar)
            // Esto satisface al backend mientras implementamos el mapa real
            val parcelaDummy = ParcelaRegistroDto(
                usos = tipoCultivo,
                localidad = localidad,
                coordenadas = listOf(
                    listOf(18.500, -88.300),
                    listOf(18.501, -88.301),
                    listOf(18.500, -88.301),
                    listOf(18.500, -88.300) // Cerrar el polígono es buena práctica
                )
            )

            // Construimos el objeto complejo Usuario -> Parcela
            val usuarioDto = UsuarioRegistroDataDto(
                nombre = nombreReal,
                apellido1 = apellido1Real,
                correo = correo,
                contrasena = contrasena,
                curp = curp,
                domicilio = localidad,
                parcela = listOf(parcelaDummy)
            )

            val request = RegisterRequestDto(usuario = usuarioDto)

            // Llamada a la API
            apiService.register(request)

            Result.success(Unit)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
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
}