package com.semilladigital.geomap.data.repository

import com.semilladigital.geomap.data.remote.GeomapApi
import com.semilladigital.geomap.data.remote.dto.toDomain
import com.semilladigital.geomap.domain.model.MapDataDomain
import com.semilladigital.geomap.domain.repository.GeomapRepository
import javax.inject.Inject

class GeomapRepositoryImpl @Inject constructor(
    private val api: GeomapApi
) : GeomapRepository {

    override suspend fun getMapData(): Result<MapDataDomain> {
        return try {
            // Llamadas a la API
            val parcelasDto = api.getParcelas()
            val ubicacionesDto = api.getUbicacionesEspeciales()

            // Mapeo a Dominio
            val parcelasDomain = parcelasDto.data.map { it.toDomain() }
            val ubicacionesDomain = ubicacionesDto.data.map { it.toDomain() }

            Result.success(MapDataDomain(parcelasDomain, ubicacionesDomain))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}