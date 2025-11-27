package com.semilladigital.supports.domain.repository

import com.semilladigital.supports.domain.model.Apoyo
import com.semilladigital.supports.domain.model.HistorialApoyoResponse
import com.semilladigital.supports.domain.model.RegistroData
import kotlinx.coroutines.flow.StateFlow

interface ApoyosRepository {
    val apoyos: StateFlow<List<Apoyo>>
    val registroUsuario: StateFlow<RegistroData?>

    suspend fun refreshApoyos(): Result<Unit>
    suspend fun getAllApoyos(forceRefresh: Boolean): Result<List<Apoyo>>

    suspend fun refreshRegistro(idUsuario: String): Result<Unit>
    suspend fun getRegistroPorUsuario(idUsuario: String, forceRefresh: Boolean): Result<RegistroData>

    suspend fun inscribirse(idApoyo: String, idParcela: String): Result<HistorialApoyoResponse>
}