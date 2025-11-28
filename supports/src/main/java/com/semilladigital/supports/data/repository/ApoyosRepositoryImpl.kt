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

    private val _registroUsuario = MutableStateFlow<RegistroData?>(null)
    override val registroUsuario: StateFlow<RegistroData?> = _registroUsuario.asStateFlow()

    private var lastFetchTimeApoyos: Long = 0
    private var lastFetchTimeRegistro: Long = 0
    private val CACHE_TIMEOUT = 5 * 60 * 1000 // 5 minutos

    override suspend fun refreshApoyos(): Result<Unit> {
        return try {
            val response = apiService.getApoyos()
            // Asignamos la lista que viene dentro de "data"
            _apoyos.value = response.data
            lastFetchTimeApoyos = System.currentTimeMillis()
            Result.success(Unit)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }

    override suspend fun getAllApoyos(forceRefresh: Boolean): Result<List<Apoyo>> {
        val isCacheValid = (System.currentTimeMillis() - lastFetchTimeApoyos) < CACHE_TIMEOUT

        if (!forceRefresh && _apoyos.value.isNotEmpty() && isCacheValid) {
            return Result.success(_apoyos.value)
        }

        return try {
            val response = apiService.getApoyos()
            _apoyos.value = response.data
            lastFetchTimeApoyos = System.currentTimeMillis()
            Result.success(response.data)
        } catch (e: Exception) {
            e.printStackTrace()
            // Si falla la red pero tenemos datos viejos, los devolvemos
            if (_apoyos.value.isNotEmpty()) {
                Result.success(_apoyos.value)
            } else {
                Result.failure(e)
            }
        }
    }

    override suspend fun refreshRegistro(idUsuario: String): Result<Unit> {
        return try {
            val registrosResponse = apiService.getTodosLosRegistros()
            val registroEncontrado = registrosResponse.data.find { it.Usuario.idUsuario == idUsuario }

            if (registroEncontrado != null) {
                _registroUsuario.value = registroEncontrado
                lastFetchTimeRegistro = System.currentTimeMillis()
                Result.success(Unit)
            } else {
                _registroUsuario.value = null
                Result.failure(Exception("Usuario no encontrado en los registros"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getRegistroPorUsuario(idUsuario: String, forceRefresh: Boolean): Result<RegistroData> {
        val isCacheValid = (System.currentTimeMillis() - lastFetchTimeRegistro) < CACHE_TIMEOUT
        val currentData = _registroUsuario.value

        if (!forceRefresh && currentData != null && currentData.Usuario.idUsuario == idUsuario && isCacheValid) {
            return Result.success(currentData)
        }

        return try {
            refreshRegistro(idUsuario)
            val newData = _registroUsuario.value
            if (newData != null) {
                Result.success(newData)
            } else {
                Result.failure(Exception("No se encontró el registro."))
            }
        } catch (e: Exception) {
            if (currentData != null) {
                Result.success(currentData)
            } else {
                Result.failure(e)
            }
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