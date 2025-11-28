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
            val safeIdRegistro = userDto.idRegistro ?: ""

            val actividadesDummy = getDummyActividades()

            sessionStorage.saveSession(
                token = response.token,
                id = userDto.id ?: "",
                nombre = userDto.nombre ?: "Usuario",
                apellidos = apellidos,
                email = userDto.correo ?: "",
                rol = safeRole,
                estatus = userDto.estatus ?: "Activo",
                actividades = actividadesDummy,
                idRegistro = safeIdRegistro
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
            val userDto = apiService.getMe()

            val apellidos = "${userDto.apellido1 ?: ""} ${userDto.apellido2 ?: ""}".trim()
            val safeRole = userDto.rol?.firstOrNull()?.nombreRol ?: "Usuario"
            val safeIdRegistro = userDto.idRegistro ?: ""

            val actividadesDummy = getDummyActividades()

            sessionStorage.saveSession(
                token = token,
                id = userDto.id ?: "",
                nombre = userDto.nombre ?: "",
                apellidos = apellidos,
                email = userDto.correo ?: "",
                rol = safeRole,
                estatus = userDto.estatus ?: "Activo",
                actividades = actividadesDummy,
                idRegistro = safeIdRegistro
            )

            Result.success(userDto.toUser())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun logout(token: String): Result<Unit> {
        return try {
            apiService.logout()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateProfile(idRegistro: String, updateData: Map<String, Any>): Result<Unit> {
        return try {
            apiService.updateRegistro(idRegistro, updateData)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun getDummyActividades(): List<String> {
        return listOf(
            "Maíz",
            "Frijol",
            "Arroz",
            "Ganado Bovino",
            "Pesca",
            "Ganaderia"
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