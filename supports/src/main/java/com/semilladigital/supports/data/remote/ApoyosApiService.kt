package com.semilladigital.supports.data.remote

import com.semilladigital.supports.domain.model.ApoyosResponse
import com.semilladigital.supports.domain.model.HistorialApoyoRequest
import com.semilladigital.supports.domain.model.HistorialApoyoResponse
import com.semilladigital.supports.domain.model.RegistroResponse
import com.semilladigital.supports.domain.model.RegistrosListResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ApoyosApiService {
    @GET("apoyo")
    suspend fun getApoyos(): ApoyosResponse

    @GET("registro/{id}")
    suspend fun getRegistroUsuario(@Path("id") idRegistro: String): RegistroResponse

    @GET("registro")
    suspend fun getTodosLosRegistros(): RegistrosListResponse

    @POST("registro/historialApoyo/{idApoyo}")
    suspend fun agregarHistorialApoyo(
        @Path("idApoyo") idApoyo: String,
        @Body request: HistorialApoyoRequest
    ): HistorialApoyoResponse
}