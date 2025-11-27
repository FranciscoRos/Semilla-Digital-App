package com.semilladigital.supports.data.repository

import com.semilladigital.supports.data.remote.ApoyosApiService
import com.semilladigital.supports.domain.model.Apoyo
import com.semilladigital.supports.domain.model.HistorialApoyoRequest
import com.semilladigital.supports.domain.model.HistorialApoyoResponse
import com.semilladigital.supports.domain.model.RegistroData
import com.semilladigital.supports.domain.repository.ApoyosRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ApoyosRepositoryImpl @Inject constructor(
    private val apiService: ApoyosApiService
) : ApoyosRepository {

    private val _apoyos = MutableStateFlow<List<Apoyo>>(emptyList())
    override val apoyos: StateFlow<List<Apoyo>> = _apoyos.asStateFlow()

    private var lastFetchTime: Long = 0
    private val CACHE_TIMEOUT = 5 * 60 * 1000

    override suspend fun refreshApoyos(): Result<Unit> {
        return try {
            val response = apiService.getApoyos()
            _apoyos.value = response.data
            lastFetchTime = System.currentTimeMillis()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getAllApoyos(forceRefresh: Boolean): Result<List<Apoyo>> {
        val isCacheValid = (System.currentTimeMillis() - lastFetchTime) < CACHE_TIMEOUT
        if (!forceRefresh && _apoyos.value.isNotEmpty() && isCacheValid) {
            return Result.success(_apoyos.value)
        }
        return try {
            refreshApoyos()
            Result.success(_apoyos.value)
        } catch (e: Exception) {
            if (_apoyos.value.isNotEmpty()) {
                Result.success(_apoyos.value)
            } else {
                Result.failure(e)
            }
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