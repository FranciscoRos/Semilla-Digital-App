package com.semilladigital.geomap.domain.repository

import com.semilladigital.geomap.domain.model.MapDataDomain

interface GeomapRepository {
    suspend fun getMapData(): Result<MapDataDomain>
}