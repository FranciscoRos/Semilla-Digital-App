package com.semilladigital.geomap.domain.use_case

import com.semilladigital.geomap.domain.model.MapDataDomain
import com.semilladigital.geomap.domain.repository.GeomapRepository
import javax.inject.Inject

class GetMapDataUseCase @Inject constructor(
    private val repository: GeomapRepository
) {
    suspend operator fun invoke(): Result<MapDataDomain> {
        return repository.getMapData()
    }
}