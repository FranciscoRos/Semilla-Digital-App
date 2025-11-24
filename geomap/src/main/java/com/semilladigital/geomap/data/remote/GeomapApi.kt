package com.semilladigital.geomap.data.remote

import com.semilladigital.geomap.data.remote.dto.ParcelasResponseDto
import com.semilladigital.geomap.data.remote.dto.UbicacionesResponseDto
import retrofit2.http.GET

interface GeomapApi {
    @GET("parcelas")
    suspend fun getParcelas(): ParcelasResponseDto

    @GET("ubicacionEspecial")
    suspend fun getUbicacionesEspeciales(): UbicacionesResponseDto
}