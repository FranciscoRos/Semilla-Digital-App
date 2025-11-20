package com.semilladigital.auth.data.repository

import com.semilladigital.app.core.data.storage.SessionStorage
import com.semilladigital.auth.data.remote.AuthApiService
import com.semilladigital.auth.data.remote.dto.LoginRequestDto
import com.semilladigital.auth.data.remote.dto.UserDto
import com.semilladigital.auth.domain.model.AuthResult
import com.semilladigital.auth.domain.model.User
import com.semilladigital.auth.domain.repository.AuthRepository
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val apiService: AuthApiService,
    private val sessionStorage: SessionStorage
) : AuthRepository {

    override suspend fun login(correo: String, contrasena: String): Result<AuthResult> {
        return try {
            val requestDto = LoginRequestDto(correo = correo, contrasena = contrasena)
            val response = apiService.login(requestDto)

            if (response.usuario == null) throw Exception("Usuario nulo en respuesta")

            val userDto = response.usuario

            val safeRole = userDto.rol?.firstOrNull()?.nombreRol ?: "Productor"
            val apellidos = "${userDto.apellido1 ?: ""} ${userDto.apellido2 ?: ""}".trim()

            // --- DUMMY DATA: Usamos datos falsos por el momento ---
            val actividadesDummy = getDummyActividades()

            sessionStorage.saveSession(
                token = response.token,
                id = userDto.id ?: "",
                nombre = userDto.nombre ?: "Usuario",
                apellidos = apellidos,
                email = userDto.correo ?: "",
                rol = safeRole,
                estatus = userDto.estatus ?: "Activo",
                actividades = actividadesDummy // <--- Inyectamos la lista falsa
            )

            val authResult = AuthResult(
                user = response.usuario.toUser(),
                token = response.token
            )
            Result.success(authResult)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun register(registrationData: Map<String, Any>): Result<Unit> {
        return try {
            apiService.register(registrationData)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getUserProfile(token: String): Result<User> {
        return try {
            val bearerToken = "Bearer $token"
            val userDto = apiService.getMe(bearerToken)

            val apellidos = "${userDto.apellido1 ?: ""} ${userDto.apellido2 ?: ""}".trim()
            val safeRole = userDto.rol?.firstOrNull()?.nombreRol ?: "Usuario"

            // --- DUMMY DATA: También aquí para mantener la consistencia ---
            val actividadesDummy = getDummyActividades()

            sessionStorage.saveSession(
                token = token,
                id = userDto.id ?: "",
                nombre = userDto.nombre ?: "",
                apellidos = apellidos,
                email = userDto.correo ?: "",
                rol = safeRole,
                estatus = userDto.estatus ?: "Activo",
                actividades = actividadesDummy // <--- Inyectamos la lista falsa
            )

            Result.success(userDto.toUser())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // --- FUNCIÓN DUMMY (Bórrala cuando el backend esté listo) ---
    private fun getDummyActividades(): List<String> {
        return listOf(
            "Maíz",
            "Frijol",
            "Arroz",
            "Ganado Bovino",
            "Pesca",
            "Horticultura"
        )
    }
}

private fun UserDto.toUser(): User {
    val safeId = this.id ?: "sin_id"

    return User(
        id = safeId,
        nombre = this.nombre ?: "Usuario",
        correo = this.correo ?: "",
        nombreRol = this.rol?.firstOrNull()?.nombreRol ?: "Productor",
        estatus = this.estatus ?: "Desconocido"
    )
}