package com.semilladigital.supports.domain.repository

import com.semilladigital.supports.domain.model.Apoyo
import com.semilladigital.supports.domain.model.HistorialApoyoResponse
import com.semilladigital.supports.domain.model.RegistroData

interface ApoyosRepository {
    suspend fun getAllApoyos(): Result<List<Apoyo>>
    suspend fun getRegistroPorUsuario(idUsuario: String): Result<RegistroData>
    suspend fun inscribirse(idApoyo: String, idParcela: String): Result<HistorialApoyoResponse>
}