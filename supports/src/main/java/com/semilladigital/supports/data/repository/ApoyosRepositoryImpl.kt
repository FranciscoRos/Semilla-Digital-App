package com.semilladigital.supports.data.repository

import com.semilladigital.supports.data.remote.ApoyosApiService
import com.semilladigital.supports.domain.model.Apoyo
import com.semilladigital.supports.domain.model.HistorialApoyoRequest
import com.semilladigital.supports.domain.model.HistorialApoyoResponse
import com.semilladigital.supports.domain.model.RegistroData
import com.semilladigital.supports.domain.repository.ApoyosRepository
import javax.inject.Inject

class ApoyosRepositoryImpl @Inject constructor(
    private val apiService: ApoyosApiService
) : ApoyosRepository {

    override suspend fun getAllApoyos(): Result<List<Apoyo>> {
        return try {
            val response = apiService.getApoyos()
            Result.success(response.data)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getRegistroPorUsuario(idUsuario: String): Result<RegistroData> {
        return try {
            val response = apiService.getTodosLosRegistros()
            val registroEncontrado = response.data.find { it.Usuario.idUsuario == idUsuario }

            if (registroEncontrado != null) {
                Result.success(registroEncontrado)
            } else {
                Result.failure(Exception("No se encontró un registro asociado a este usuario."))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun inscribirse(idApoyo: String, idParcela: String): Result<HistorialApoyoResponse> {
        return try {
            val request = HistorialApoyoRequest(parcelaId = idParcela)
            val response = apiService.agregarHistorialApoyo(idApoyo, request)

            if (response.success) {
                Result.success(response)
            } else {
                Result.failure(Exception(response.message))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}