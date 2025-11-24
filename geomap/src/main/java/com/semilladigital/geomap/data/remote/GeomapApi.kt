package com.semilladigital.geomap.data.remote

import com.semilladigital.geomap.data.ParcelasResponse
import com.semilladigital.geomap.data.UbicacionesResponse
import retrofit2.http.GET

interface GeomapApi {
    @GET("/api/parcelas")
    suspend fun getParcelas(): ParcelasResponse

    @GET("/api/ubicacionEspecial")
    suspend fun getUbicacionesEspeciales(): UbicacionesResponse
}