package com.semilladigital.auth.data.repository

import com.semilladigital.auth.data.remote.AuthApiService
import com.semilladigital.auth.data.remote.dto.LoginRequestDto
import com.semilladigital.auth.data.remote.dto.RegisterRequestDto
import com.semilladigital.auth.data.remote.dto.UsuarioRegistroDataDto
import com.semilladigital.auth.data.remote.dto.ParcelaRegistroDto
import com.semilladigital.auth.data.remote.dto.UserDto
import com.semilladigital.auth.domain.model.AuthResult
import com.semilladigital.auth.domain.model.User
import com.semilladigital.auth.domain.model.UsoItem
import com.semilladigital.auth.domain.repository.AuthRepository
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val api: AuthApiService
) : AuthRepository {

    override suspend fun login(correo: String, contrasena: String): Result<AuthResult> {
        return try {
            val response = api.login(LoginRequestDto(correo, contrasena))
            val userDomain = response.usuario.toDomain()
            val authResult = AuthResult(user = userDomain, token = response.token)
            Result.success(authResult)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }

    override suspend fun getUserProfile(token: String): Result<User> {
        return try {
            val userDto = api.me("Bearer $token")
            val userDomain = userDto.toDomain()
            Result.success(userDomain)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }

    // --- AQUÍ CONSTRUIMOS EL JSON COMPLEJO ---
    override suspend fun register(registrationData: Map<String, Any>): Result<Unit> {
        return try {
            // 1. Extraer datos del mapa plano (UI)
            val nombre = registrationData["Nombre"] as? String ?: ""
            val correo = registrationData["Correo"] as? String ?: ""
            val contrasena = registrationData["Contrasena"] as? String ?: ""
            val curp = registrationData["Curp"] as? String ?: ""
            val telefono = registrationData["Telefono"] as? String ?: ""
            val localidad = registrationData["Localidad"] as? String ?: "" // Usado como Domicilio

            // Datos de Parcela
            val lat = registrationData["Latitud"] as? Double ?: 0.0
            val lng = registrationData["Longitud"] as? Double ?: 0.0
            val tipoCultivo = registrationData["TipoCultivo"] as? String ?: ""
            val hectareas = registrationData["Hectareas"] as? String ?: "0"

            // 2. Construir la Parcela (Lista de Coordenadas [[lat, lng]])
            val parcelaDto = ParcelaRegistroDto(
                nombre = "Parcela de $nombre",
                coordenadas = listOf(listOf(lat, lng)),
                localidad = localidad,
                area = hectareas,
                usos = tipoCultivo
            )

            // 3. Construir el Usuario anidado
            val usuarioDto = UsuarioRegistroDataDto(
                nombre = nombre,
                apellido1 = "", // Enviamos vacío como se indicó
                apellido2 = "",
                correo = correo,
                contrasena = contrasena,
                curp = curp,
                telefono = telefono,
                domicilio = localidad, // Mapeamos Localidad -> Domicilio
                parcela = listOf(parcelaDto) // Lista de parcelas
            )

            // 4. Crear el Request Final
            val request = RegisterRequestDto(usuario = usuarioDto)

            // 5. Enviar
            api.register(request)

            Result.success(Unit)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }

    private fun UserDto.toDomain(): User {
        return User(
            id = id ?: "",
            nombre = nombre ?: "",
            apellido1 = apellido1 ?: "",
            apellido2 = apellido2 ?: "",
            correo = correo ?: "",
            estatus = estatus ?: "Activo",
            usos = usos?.map { dto ->
                UsoItem(
                    usoGeneral = dto.usoGeneral ?: "",
                    usosEspecificos = dto.usosEspecificos ?: emptyList()
                )
            } ?: emptyList()
        )
    }
}