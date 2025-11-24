package com.semilladigital.geomap.data

import com.semilladigital.geomap.data.remote.GeomapApi
import javax.inject.Inject

class GeomapRepository @Inject constructor(
    private val api: GeomapApi
) {
    suspend fun getMapData(): Result<MapData> {
        return try {
            // Hacemos las llamadas en paralelo o secuencial
            // (Para simplicidad, secuencial aquí, pero idealmente async/await)
            val parcelas = api.getParcelas().data
            val ubicaciones = api.getUbicacionesEspeciales().data

            Result.success(MapData(parcelas, ubicaciones))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

// Clase de dominio simple para la UI
data class MapData(
    val parcelas: List<ParcelaDto>,
    val ubicaciones: List<UbicacionEspecialDto>
)